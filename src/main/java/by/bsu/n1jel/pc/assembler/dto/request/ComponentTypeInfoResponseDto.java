package by.bsu.n1jel.pc.assembler.dto.request;

public record ComponentTypeInfoResponseDto(
        Long id,
        String name,
        Long componentParentType
) {
}
