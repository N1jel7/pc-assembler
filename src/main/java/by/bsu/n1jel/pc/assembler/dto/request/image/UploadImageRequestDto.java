package by.bsu.n1jel.pc.assembler.dto.request.image;

import by.bsu.n1jel.pc.assembler.entity.enums.ObjectType;
import org.springframework.web.multipart.MultipartFile;

public record UploadImageRequestDto(
        MultipartFile file,
        Long objectId,
        ObjectType objectType

) {
}
