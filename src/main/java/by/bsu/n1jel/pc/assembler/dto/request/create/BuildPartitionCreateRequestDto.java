package by.bsu.n1jel.pc.assembler.dto.request.create;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BuildPartitionCreateRequestDto {
    Long componentId;
    Long buildId;
    Integer quantity;
}