package by.bsu.n1jel.pc.assembler.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BuildInfoResponseDto{
    private Long buildId;
    private String name;
    private LocalDateTime creationDate;
    private String price;
    private Integer filledSlots;
    private List<BuildPartitionInfoResponseDto> partitions;
    private String avatar;
}
