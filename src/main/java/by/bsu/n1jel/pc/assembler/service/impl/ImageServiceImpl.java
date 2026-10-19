package by.bsu.n1jel.pc.assembler.service.impl;

import by.bsu.n1jel.pc.assembler.dto.request.image.DeleteImageRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.image.LoadImageRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.ImageContentResponseDto;
import by.bsu.n1jel.pc.assembler.dto.response.ImageInfoResponseDto;
import by.bsu.n1jel.pc.assembler.entity.Image;
import by.bsu.n1jel.pc.assembler.entity.enums.ObjectType;
import by.bsu.n1jel.pc.assembler.exception.ImageStorageException;
import by.bsu.n1jel.pc.assembler.exception.ImageValidationException;
import by.bsu.n1jel.pc.assembler.repository.ImageRepository;
import by.bsu.n1jel.pc.assembler.service.api.ImageService;
import by.bsu.n1jel.pc.assembler.service.utils.ImageUrlUtil;
import by.bsu.n1jel.pc.assembler.service.utils.OverallUtil;
import by.bsu.n1jel.pc.assembler.service.utils.UploadUtil;
import io.minio.*;
import io.minio.errors.MinioException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static by.bsu.n1jel.pc.assembler.exception.common.ResourceExceptionFactory.imageNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private static final Map<ObjectType, String> BUCKET_BY_TYPE = Map.of(
            ObjectType.PRODUCER, "producer",
            ObjectType.COMPONENT, "component",
            ObjectType.BUILD, "build"
    );

    // без SVG: картинки отдаются с нашего домена, а SVG может содержать скрипты
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );

    private final MinioClient minioClient;
    private final ImageRepository imageRepository;

    @PostConstruct
    public void initializeBuckets() {
        for (String bucket : BUCKET_BY_TYPE.values()) {
            try {
                if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                }
            } catch (Exception e) {
                // приложение должно подниматься, даже если MinIO пока недоступен
                log.warn("Cannot initialize bucket {}: {}", bucket, e.getMessage());
            }
        }
    }

    private Image findImageById(Long imageId) {
        return imageRepository.findById(imageId)
                .orElseThrow(
                        () -> imageNotFoundException(imageId)
                );
    }

    @Override
    @Transactional
    public ImageInfoResponseDto delete(DeleteImageRequestDto request) {
        Image image = findImageById(request.id());
        ImageInfoResponseDto responseDto = ImageUrlUtil.toInfo(image);

        imageRepository.unlinkFromComponents(image.getId());
        imageRepository.unlinkFromBuilds(image.getId());
        imageRepository.unlinkFromProducers(image.getId());
        imageRepository.delete(image);
        removeFromStorage(List.of(image));

        return responseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public String load(LoadImageRequestDto requestDto) {
        return Base64.getEncoder().encodeToString(loadContent(requestDto.id()).bytes());
    }

    @Override
    @Transactional(readOnly = true)
    public ImageContentResponseDto loadContent(Long imageId) {
        Image image = findImageById(imageId);

        try (InputStream inputStream = minioClient.getObject(GetObjectArgs.builder()
                .bucket(image.getBucket())
                .object(image.getLocation())
                .build())) {
            return new ImageContentResponseDto(inputStream.readAllBytes(), image.getContentType());
        } catch (MinioException | IOException e) {
            throw new ImageStorageException("Can't load image id = %s from storage".formatted(imageId), e);
        }
    }

    @Override
    @Transactional
    public Image store(MultipartFile file, ObjectType objectType, Long objectId) {
        validate(file);

        String bucket = BUCKET_BY_TYPE.get(objectType);
        // UUID в имени — чтобы одинаковые имена файлов не перезаписывали друг друга
        String location = objectId + "/" + UUID.randomUUID() + extensionOf(file.getOriginalFilename());

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(location)
                    .stream(inputStream, file.getSize(), -1L)
                    .contentType(file.getContentType())
                    .build());
        } catch (MinioException | IOException e) {
            throw new ImageStorageException("Can't upload image to bucket " + bucket, e);
        }

        Image image = imageRepository.save(Image.builder()
                .bucket(bucket)
                .location(location)
                .contentType(file.getContentType())
                .build());

        runOnRollback(() -> removeObject(bucket, location));
        return image;
    }

    @Override
    @Transactional
    public void syncImages(List<Image> images, Collection<Long> keptImageIds, List<MultipartFile> newFiles,
                           ObjectType objectType, Long objectId) {
        Set<Long> kept = keptImageIds == null ? Set.of() : new HashSet<>(keptImageIds);

        List<Image> removed = images.stream()
                .filter(image -> !kept.contains(image.getId()))
                .toList();
        images.removeAll(removed);
        removeFromStorage(removed);

        int freeSlots = Math.max(OverallUtil.getMaxImagesSize() - images.size(), 0);
        UploadUtil.nonEmpty(newFiles).stream()
                .limit(freeSlots)
                .forEach(file -> images.add(store(file, objectType, objectId)));
    }

    @Override
    public void removeFromStorage(Collection<Image> images) {
        // копируем координаты сразу: после коммита сущности уже могут быть удалены
        List<String[]> objects = images.stream()
                .map(image -> new String[]{image.getBucket(), image.getLocation()})
                .toList();
        runAfterCommit(() -> objects.forEach(object -> removeObject(object[0], object[1])));
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ImageValidationException("Файл не выбран или пустой");
        }
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new ImageValidationException("Можно загружать только изображения JPEG, PNG, WebP или GIF");
        }
    }

    private static String extensionOf(String filename) {
        String extension = StringUtils.getFilenameExtension(filename);
        return extension == null ? "" : "." + extension.toLowerCase(Locale.ROOT);
    }

    private void removeObject(String bucket, String location) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(location)
                    .build());
        } catch (Exception e) {
            log.error("Can't delete {}/{} from storage: {}", bucket, location, e.getMessage());
        }
    }

    private static void runAfterCommit(Runnable action) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            action.run();
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                action.run();
            }
        });
    }

    private static void runOnRollback(Runnable action) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_ROLLED_BACK) {
                    action.run();
                }
            }
        });
    }
}
