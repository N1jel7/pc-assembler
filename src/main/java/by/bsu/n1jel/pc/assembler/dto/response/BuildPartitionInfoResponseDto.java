package by.bsu.n1jel.pc.assembler.dto.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BuildPartitionInfoResponseDto {
    Long buildPartitionId;
    Long buildId;

    Long componentId;
    String componentName;
    Long componentTypeId;
    String componentTypeName;
    String producerName;
    String componentImage;

    Integer quantity;
    Integer minQuantity;
    Integer maxQuantity;
    Boolean fixedQuantity;

    BigDecimal componentPrice;
    BigDecimal subtotal;
}
