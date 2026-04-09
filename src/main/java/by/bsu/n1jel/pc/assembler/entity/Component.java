package by.bsu.n1jel.pc.assembler.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "components")
@Data
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
    @OneToMany(mappedBy = "componentId",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ComponentSpecification> specifications;

    @Column(name = "image_url")
    private String imageUrl;
}
