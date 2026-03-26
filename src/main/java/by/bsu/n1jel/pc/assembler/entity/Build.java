package by.bsu.n1jel.pc.assembler.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;

@RequiredArgsConstructor
@Entity
@Table(name = "builds")
@Data
public class Build {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "creation_date")
    private Timestamp creationDate;
}
