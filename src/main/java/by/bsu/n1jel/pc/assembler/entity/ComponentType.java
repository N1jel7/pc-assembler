package by.bsu.n1jel.pc.assembler.entity;

import jakarta.persistence.*;
import lombok.*;

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
}
