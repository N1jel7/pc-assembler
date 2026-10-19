package by.bsu.n1jel.pc.assembler.dto.request.create;

public record ComponentTypeCreateRequestDto(
        String name,
        Integer minAmount,
        Integer maxAmount
) {
}
