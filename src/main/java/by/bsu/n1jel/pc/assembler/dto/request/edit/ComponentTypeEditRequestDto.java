package by.bsu.n1jel.pc.assembler.dto.request.edit;

public record ComponentTypeEditRequestDto(
        Long id,
        String name,
        Long parentType
) {
}
