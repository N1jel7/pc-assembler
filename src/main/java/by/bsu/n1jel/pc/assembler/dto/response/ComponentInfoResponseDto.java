package by.bsu.n1jel.pc.assembler.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record ComponentInfoResponseDto(
        Long id,
        String name,
        Long producer,
        Long componentType,
        BigDecimal price,
        Integer stockQuantity,
        List<ComponentSpecificationInfoResponseDto> specifications
) {
}
