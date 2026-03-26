package by.bsu.n1jel.pc.assembler.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Entity
@Table(name = "component_types")
@Data
public class ComponentType {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToOne
    private ComponentType componentParentType;
}
