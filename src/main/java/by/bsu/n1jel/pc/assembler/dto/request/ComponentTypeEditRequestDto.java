package by.bsu.n1jel.pc.assembler.dto.request;

public record ComponentTypeEditRequestDto(
        Long id,
        String name,
        Long componentParentType
) {
}
