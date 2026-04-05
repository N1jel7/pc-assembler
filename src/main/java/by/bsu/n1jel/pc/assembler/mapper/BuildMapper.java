package by.bsu.n1jel.pc.assembler.mapper;

import by.bsu.n1jel.pc.assembler.dto.response.BuildInfoResponseDto;
import by.bsu.n1jel.pc.assembler.entity.Build;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BuildMapper {

    BuildInfoResponseDto mapToResponseDto(Build build);

    List<BuildInfoResponseDto> mapToResponseDto(List<Build> builds);
}
