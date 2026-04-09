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

    @ToString.Exclude // TODO временно
    @OneToOne
    @JoinColumn(name = "id")
    private ComponentType componentParentType;
}
