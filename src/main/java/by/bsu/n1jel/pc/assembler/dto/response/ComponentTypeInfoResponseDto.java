package by.bsu.n1jel.pc.assembler.dto.response;

import by.bsu.n1jel.pc.assembler.entity.enums.ComponentTypeCode;

import java.util.List;

public record ComponentTypeInfoResponseDto(
        Long id,
        String name,
        ComponentTypeCode code,
        List<Long> requiredSpecificationTypeIds
) {
}
