package by.bsu.n1jel.pc.assembler.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "producers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Producer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "country")
    private String country;

    @Column(name = "logo_url")
    private String logoUrl;
}
