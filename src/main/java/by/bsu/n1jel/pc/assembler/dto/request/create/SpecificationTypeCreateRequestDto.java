package by.bsu.n1jel.pc.assembler.dto.request.create;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpecificationTypeCreateRequestDto {
    String name;
    String description;
}
