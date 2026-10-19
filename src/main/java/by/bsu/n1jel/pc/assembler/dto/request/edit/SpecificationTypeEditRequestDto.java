package by.bsu.n1jel.pc.assembler.dto.request.edit;

import by.bsu.n1jel.pc.assembler.entity.enums.SpecValueType;

public record SpecificationTypeEditRequestDto(
        Long id,
        String name,
        String description,
        SpecValueType valueType,
        String unit
) {
}
