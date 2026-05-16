package by.bsu.n1jel.pc.assembler.dto.request.create;

public record BuildPartitionCreateRequestDto(
        Long componentId,
        Long buildId,
        Integer quantity
) {
}
