package by.bsu.n1jel.pc.assembler.dto.response;

public record BuildPartitionInfoResponseDto (
        Long buildPartitionId,
        Long buildId,
        Long componentId,
        Integer quantity
){
}
