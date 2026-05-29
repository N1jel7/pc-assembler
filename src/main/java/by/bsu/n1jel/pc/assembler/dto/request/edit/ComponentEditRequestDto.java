package by.bsu.n1jel.pc.assembler.dto.request.edit;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ComponentEditRequestDto {
    Long id;
    String name;
    Long producer;
    Long componentType;
    BigDecimal price;
    Integer stockQuantity;
    List<SpecificationEditRequestDto> specifications;
}
