package by.bsu.n1jel.pc.assembler.dto.response;

public record StatsInfoResponseDto(
        Long componentsCount,
        Long buildsCount,
        Long producersCount,
        Long specificationTypesCount,
        Long buildItemsCount) {
}
