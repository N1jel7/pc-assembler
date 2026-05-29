package by.bsu.n1jel.pc.assembler.dto.request.search;

import java.math.BigDecimal;

public record BuildFilterRequestDto(
        String query,
        String processor,
        BigDecimal priceFrom,
        BigDecimal priceTo
) {
}
