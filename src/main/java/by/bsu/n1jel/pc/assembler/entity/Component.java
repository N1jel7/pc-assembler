package by.bsu.n1jel.pc.assembler.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Компонент
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "components")
public class Component {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Producer producer;

    @ManyToOne(optional = false)
    private ComponentType componentType;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @ToString.Exclude // TODO временно
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "components_specifications",
            joinColumns = @JoinColumn(name = "component_id")
    )
    private List<Specification> specifications;

    @Column(name = "image_url")
    private String imageUrl;
}
