package by.bsu.n1jel.pc.assembler.dto.request.edit;

public record BuildPartitionEditRequestDto(
        Long buildPartitionId,
        Long componentId,
        Integer quantity
) {
}
