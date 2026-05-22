package by.bsu.n1jel.pc.assembler.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class BuildInfoResponseDto{
    private Long buildId;
    private String name;
    private String price;
    private String creationDate;
    private List<BuildPartitionInfoResponseDto> partitions;
}
