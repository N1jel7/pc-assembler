package by.bsu.n1jel.pc.assembler.dto.response;

import by.bsu.n1jel.pc.assembler.entity.enums.SpecValueType;

public record SpecificationTypeInfoResponseDto(
        Long id,
        String name,
        String description,
        String code,
        SpecValueType valueType,
        String unit
){
}
