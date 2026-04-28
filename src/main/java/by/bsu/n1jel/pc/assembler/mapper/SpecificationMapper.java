package by.bsu.n1jel.pc.assembler.mapper;

import by.bsu.n1jel.pc.assembler.dto.request.edit.SpecificationEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.SpecificationTypeEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.SpecificationTypeInfoResponseDto;
import by.bsu.n1jel.pc.assembler.entity.Specification;
import by.bsu.n1jel.pc.assembler.entity.SpecificationType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import java.util.List;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = IGNORE)
public interface SpecificationMapper {

    SpecificationTypeInfoResponseDto mapToResponse(Specification specification);
    
    List<SpecificationTypeInfoResponseDto> mapToResponse(List<Specification> specifications);

    SpecificationTypeInfoResponseDto mapTypeToResponse(SpecificationType specification);

    List<SpecificationTypeInfoResponseDto> mapTypeToResponse(List<SpecificationType> specifications);

    Specification updateSpecification(@MappingTarget Specification specification, SpecificationEditRequestDto requestDto);

    SpecificationType updateSpecificationType(@MappingTarget SpecificationType specificationType, SpecificationTypeEditRequestDto requestDto);
}
