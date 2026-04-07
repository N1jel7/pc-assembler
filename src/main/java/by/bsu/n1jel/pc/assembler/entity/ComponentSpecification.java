package by.bsu.n1jel.pc.assembler.entity;

import by.bsu.n1jel.pc.assembler.entity.composite.ComponentSpecificationCompositeKey;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@IdClass(ComponentSpecificationCompositeKey.class)
@Table(name = "components_specifications")
@NoArgsConstructor
@AllArgsConstructor
public class ComponentSpecification {

    @Id
    private Long componentId;

    @Id
    private Long specificationId;

    @Column(name = "value")
    private String value;

}
