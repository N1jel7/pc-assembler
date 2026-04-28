package by.bsu.n1jel.pc.assembler.dto.request.edit;

public record BuildPartitionEditRequestDto(
        Long buildId,
        Long componentId,
        Integer quantity
) {
}
