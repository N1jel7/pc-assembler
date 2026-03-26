package by.bsu.n1jel.pc.assembler.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "producers")
@Data
@RequiredArgsConstructor
public class Producer {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "country")
    private String country;

    @Column(name = "logo_url")
    private String logoUrl;
}
