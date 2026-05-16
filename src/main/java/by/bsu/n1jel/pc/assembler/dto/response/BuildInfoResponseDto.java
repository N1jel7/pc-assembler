package by.bsu.n1jel.pc.assembler.dto.response;

import java.util.List;

public record BuildInfoResponseDto(
        Long buildId,
        String name,
        List<BuildPartitionInfoResponseDto> partitions
) {
}
