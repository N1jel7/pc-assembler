package by.bsu.n1jel.pc.assembler.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@RequiredArgsConstructor
@AllArgsConstructor
public class Specification {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Component component;
}
