package by.bsu.n1jel.pc.assembler.dto.request;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public record ComponentCreateRequestDto(
        String name,
        Long producer,
        Long componentType,
        BigDecimal price,
        Integer stockQuantity,
        List<ComponentSpecificationCreateRequestDto> specifications
) {
}
