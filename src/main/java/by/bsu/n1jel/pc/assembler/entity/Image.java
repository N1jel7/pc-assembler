package by.bsu.n1jel.pc.assembler.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "images")
@Data
public class Image {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "bucket")
    private String bucket;

    @Column(name = "location")
    private String location;
}
