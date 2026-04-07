package by.bsu.n1jel.pc.assembler.entity.composite;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@RequiredArgsConstructor
@Getter
@Setter
@Embeddable
public class ComponentSpecificationCompositeKey implements Serializable {

    private Long componentId;

    private Long specificationId;

}
