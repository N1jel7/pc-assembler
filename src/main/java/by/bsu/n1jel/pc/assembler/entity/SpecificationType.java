package by.bsu.n1jel.pc.assembler.entity;

import by.bsu.n1jel.pc.assembler.entity.enums.SpecValueType;
import jakarta.persistence.*;
import lombok.*;

/**
 *  Тип характеристики
 */

@Data
@Builder
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "specification_types")
public class SpecificationType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    /**
     * Машинный код для проверок совместимости. У пользовательских типов отсутствует.
     */
    @Column(name = "code", unique = true)
    private String code;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "value_type", nullable = false)
    private SpecValueType valueType = SpecValueType.STRING;

    @Column(name = "unit")
    private String unit;
}
