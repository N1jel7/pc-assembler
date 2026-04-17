package by.bsu.n1jel.pc.assembler.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ComponentInfoResponseDto {
        private Long id;
        private String name;
        private Long producer;
        private Long componentType;
        private BigDecimal price;
        private Integer stockQuantity;
        private List<SpecificationInfoResponseDto> specifications;
}
