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
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Build build;

    @ManyToOne
    private Component component;

    @Column(name = "quantity")
    private Integer quantity;
}
