package by.bsu.n1jel.pc.assembler.dto.response;

public record SpecificationInfoResponseDto(
        Long id,
        Long specificationTypeId,
        String name,
        String description,
        String value
) {
}
