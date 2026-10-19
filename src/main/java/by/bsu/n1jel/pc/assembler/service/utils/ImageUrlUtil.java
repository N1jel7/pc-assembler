package by.bsu.n1jel.pc.assembler.service.utils;

import by.bsu.n1jel.pc.assembler.dto.response.ImageInfoResponseDto;
import by.bsu.n1jel.pc.assembler.entity.Image;

import java.util.List;

public class ImageUrlUtil {

    /**
     * Картинки отдаёт само приложение (ImageController), MinIO наружу не открыт.
     */
    public static String url(Long imageId) {
        return "/images/" + imageId;
    }

    public static ImageInfoResponseDto toInfo(Image image) {
        return new ImageInfoResponseDto(image.getId(), url(image.getId()));
    }

    public static List<ImageInfoResponseDto> toInfo(List<Image> images) {
        if (images == null) {
            return List.of();
        }
        return images.stream().map(ImageUrlUtil::toInfo).toList();
    }
}
