package by.bsu.n1jel.pc.assembler.dto.request.search;

import java.math.BigDecimal;

public record ComponentFilterRequestDto(
        String query,
        Long producer,
        Long componentType,
        BigDecimal priceFrom,
        BigDecimal priceTo,
        Boolean inStock
) {
}
