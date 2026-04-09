package by.bsu.n1jel.pc.assembler.dto.request;

public record ComponentTypeCreateRequestDto(
        String name,
        Long componentParentType
) {
}
