package by.bsu.n1jel.pc.assembler.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Entity
@Table(name = "build_partitions")
@Data
public class BuildPartition {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional = false)
    private Build build;

    @ManyToOne(optional = false)
    private Component component;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}
