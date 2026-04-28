package by.bsu.n1jel.pc.assembler.dto.response;

public record BuildPartitionInfoResponseDto (
        Long partitionId,
        Long componentId,
        Integer quantity,
        Long buildId
){
}
