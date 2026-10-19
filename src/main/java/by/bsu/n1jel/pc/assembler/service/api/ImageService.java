package by.bsu.n1jel.pc.assembler.service.api;

import by.bsu.n1jel.pc.assembler.dto.request.image.DeleteImageRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.image.LoadImageRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.ImageContentResponseDto;
import by.bsu.n1jel.pc.assembler.dto.response.ImageInfoResponseDto;
import by.bsu.n1jel.pc.assembler.entity.Image;
import by.bsu.n1jel.pc.assembler.entity.enums.ObjectType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;

public interface ImageService {

    ImageInfoResponseDto delete(DeleteImageRequestDto request);

    /**
     * Картинка в base64 (старый REST-эндпоинт).
     */
    String load(LoadImageRequestDto requestDto);

    ImageContentResponseDto loadContent(Long imageId);

    /**
     * Кладёт файл в MinIO и сохраняет запись Image. Если транзакция откатится, файл удаляется из MinIO.
     */
    Image store(MultipartFile file, ObjectType objectType, Long objectId);

    /**
     * Обновляет список фото сущности по данным формы: оставляет keptImageIds, добавляет новые файлы
     * (сверх лимита — отбрасываются). Список меняется на месте, так как это коллекция Hibernate.
     */
    void syncImages(List<Image> images, Collection<Long> keptImageIds, List<MultipartFile> newFiles,
                    ObjectType objectType, Long objectId);

    /**
     * Удаляет файлы из MinIO после успешного коммита текущей транзакции.
     */
    void removeFromStorage(Collection<Image> images);
}
