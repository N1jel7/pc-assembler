package by.bsu.n1jel.pc.assembler.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Характеристика компонента
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "specifications")
public class Specification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ToString.Exclude // TODO временно
    @ManyToOne(optional = false)
    private SpecificationType type;

    @ToString.Exclude // TODO временно
    @ManyToOne(optional = false)
    private Component component;

    @Column(name = "value", nullable = false)
    private String value;

}
