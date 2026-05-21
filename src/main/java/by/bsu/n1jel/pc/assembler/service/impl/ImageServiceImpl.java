package by.bsu.n1jel.pc.assembler.service.impl;

import by.bsu.n1jel.pc.assembler.dto.request.image.DeleteImageRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.image.LoadImageRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.image.UploadImageRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.ImageInfoResponseDto;
import by.bsu.n1jel.pc.assembler.entity.Image;
import by.bsu.n1jel.pc.assembler.entity.enums.ObjectType;
import by.bsu.n1jel.pc.assembler.repository.ImageRepository;
import by.bsu.n1jel.pc.assembler.service.api.ImageService;
import io.minio.*;
import io.minio.errors.MinioException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;

import static by.bsu.n1jel.pc.assembler.exception.common.ResourceExceptionFactory.imageNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final static String PRODUCER_BUCKET_NAME = "producer";
    private final static String COMPONENT_BUCKET_NAME = "component";

    private HashMap<ObjectType, String> bucketByType;

    private final MinioClient minioClient;
    private final ImageRepository imageRepository;


    @PostConstruct
    public void initializeBuckets() {

        bucketByType = new HashMap<>();
        bucketByType.put(ObjectType.PRODUCER, PRODUCER_BUCKET_NAME);
        bucketByType.put(ObjectType.COMPONENT, COMPONENT_BUCKET_NAME);

        try {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(PRODUCER_BUCKET_NAME)
                    .build());

            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(COMPONENT_BUCKET_NAME)
                    .build());

        } catch (MinioException e) {
            log.warn("Cannot initialize default buckets: {}", e.getMessage());
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
        try {
            Image image = findImageById(request.id());

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(image.getBucket())
                            .object(image.getLocation())
                            .build()
            );
            ImageInfoResponseDto responseDto = new ImageInfoResponseDto(image.getId());

            imageRepository.delete(image);
            return responseDto;
        } catch (MinioException e) {
            log.error("Can't delete image from bucket, error message: {}", e.getMessage());
        }
        return null;
    }

    @Override
    @Transactional
    public ImageInfoResponseDto upload(UploadImageRequestDto request) {
        try {
            String filename = request.file().getOriginalFilename();
            String location = request.objectId() + "/" + filename;

            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucketByType.get(request.objectType()))
                    .object(location)
                    .stream(request.file().getInputStream(), request.file().getSize(), -1L)
                    .contentType(request.file().getContentType())
                    .build();

            minioClient.putObject(putObjectArgs);

            Image savedImage = imageRepository.save(Image.builder()
                    .bucket(putObjectArgs.bucket())
                    .location(putObjectArgs.object())
                    .build()
            );
            return new ImageInfoResponseDto(savedImage.getId());
        } catch (MinioException | IOException e) {
            log.error("Can't upload image to bucket, error message: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public String load(LoadImageRequestDto requestDto) {
        try {
            Image image = findImageById(requestDto.id());

            InputStream inputStream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(image.getBucket())
                    .object(image.getLocation())
                    .build());
            return Base64.getEncoder().encodeToString(inputStream.readAllBytes());
        } catch (MinioException | IOException e) {
            log.error("Can load image from bucket, error message: {}", e.getMessage());
        }
        return null;
    }
}
