package by.bsu.n1jel.pc.assembler.entity;

import by.bsu.n1jel.pc.assembler.entity.enums.ComponentTypeCode;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "component_types")
@Data
public class ComponentType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "max_amount", nullable = false)
    private Integer maxAmount;

    @Column(name = "min_amount", nullable = false)
    private Integer minAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "code", unique = true)
    private ComponentTypeCode code;

    /**
     * Характеристики, которые нужны проверкам совместимости для компонентов этого типа.
     */
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "component_type_specifications",
            joinColumns = @JoinColumn(name = "component_type_id"),
            inverseJoinColumns = @JoinColumn(name = "specification_type_id")
    )
    private List<SpecificationType> requiredSpecifications = new ArrayList<>();
}
