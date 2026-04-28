package by.bsu.n1jel.pc.assembler.dto.request.create;

import java.math.BigDecimal;
import java.util.List;

public record ComponentCreateRequestDto(
        String name,
        Long producer,
        Long componentType,
        BigDecimal price,
        Integer stockQuantity,
        List<SpecificationCreateRequestDto> specifications
) {
}
