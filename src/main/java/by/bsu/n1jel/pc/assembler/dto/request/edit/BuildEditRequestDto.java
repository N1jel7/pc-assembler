package by.bsu.n1jel.pc.assembler.dto.request.edit;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BuildEditRequestDto {
    Long buildId;
    String name;
    /**
     * id партиции → количество (форма шлёт quantities[id]=2).
     */
    Map<Long, Integer> quantities;

    public BuildEditRequestDto(Long buildId, String name) {
        this.buildId = buildId;
        this.name = name;
    }
}