package by.bsu.n1jel.pc.assembler.dto.request.search;

import java.math.BigDecimal;

public record ComponentSearchFilterRequestDto(
        String name,
        Long producer,
        Long componentType,
        BigDecimal priceFrom,
        BigDecimal priceTo,
        Boolean inStock
) {
}
