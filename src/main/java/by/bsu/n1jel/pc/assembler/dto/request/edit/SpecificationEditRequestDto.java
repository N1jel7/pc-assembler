package by.bsu.n1jel.pc.assembler.dto.request.edit;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpecificationEditRequestDto {
    /**
     * id существующей характеристики (REST).
     */
    Long id;
    /**
     * Тип характеристики (форма): у компонента одна характеристика каждого типа.
     */
    Long specificationTypeId;
    String value;
}
