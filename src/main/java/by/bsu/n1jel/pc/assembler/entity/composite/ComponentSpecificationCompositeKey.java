package by.bsu.n1jel.pc.assembler.entity.composite;

import by.bsu.n1jel.pc.assembler.entity.Component;
import by.bsu.n1jel.pc.assembler.entity.SpecificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@RequiredArgsConstructor
@Getter
@Setter
@Embeddable
@AllArgsConstructor
public class ComponentSpecificationCompositeKey implements Serializable {

    @OneToOne
    private Component component;

    @OneToOne
    private SpecificationType specification;

}
