package by.bsu.n1jel.pc.assembler.mapper;

import by.bsu.n1jel.pc.assembler.dto.request.SpecificationEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.SpecificationInfoResponseDto;
import by.bsu.n1jel.pc.assembler.entity.Specification;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import java.util.List;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = IGNORE)
public interface SpecificationMapper {

    SpecificationInfoResponseDto mapToResponse(Specification specification);

    List<SpecificationInfoResponseDto> mapToResponse(List<Specification> specifications);

    Specification updateSpecification(@MappingTarget Specification specification, SpecificationEditRequestDto requestDto);
}
