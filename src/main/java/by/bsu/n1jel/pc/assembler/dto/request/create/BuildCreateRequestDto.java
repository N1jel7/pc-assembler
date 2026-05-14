package by.bsu.n1jel.pc.assembler.dto.request.create;

import java.util.List;

public record BuildCreateRequestDto(
        List<Long> buildPartitionIds,
        String name
) {

}