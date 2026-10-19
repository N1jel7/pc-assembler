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

    /**
     * Фото производителя — оно же его аватарка. Если нет, используется SVG-заготовка.
     */
    @ToString.Exclude
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private Image image;
}
