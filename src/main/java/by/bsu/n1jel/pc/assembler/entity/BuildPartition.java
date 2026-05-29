package by.bsu.n1jel.pc.assembler.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "build_partitions")
@Data
public class BuildPartition {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ToString.Exclude
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    private Build build;

    @ToString.Exclude // TODO временно
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH}, optional = false)
    private Component component;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}
