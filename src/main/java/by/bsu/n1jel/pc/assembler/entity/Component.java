package by.bsu.n1jel.pc.assembler.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Entity
@Table(name = "components")
@Data
public class Component {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Producer producer;

    @ManyToOne
    private ComponentType componentType;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @OneToMany
    private List<Specification> specifications;

    @Column(name = "image_url")
    private String imageUrl;
}
