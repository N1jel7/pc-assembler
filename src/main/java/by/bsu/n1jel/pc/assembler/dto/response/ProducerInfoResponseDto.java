package by.bsu.n1jel.pc.assembler.dto.response;

import lombok.Data;

@Data
public class ProducerInfoResponseDto {
    private Long id;
    private String name;
    private String country;
    /**
     * Ссылка на фото производителя, а если его нет — SVG-заготовка.
     */
    private String avatar;
    private ImageInfoResponseDto image;
}
