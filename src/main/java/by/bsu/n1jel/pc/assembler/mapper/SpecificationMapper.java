package by.bsu.n1jel.pc.assembler.mapper;

import by.bsu.n1jel.pc.assembler.dto.request.edit.SpecificationEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.SpecificationTypeEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.SpecificationInfoResponseDto;
import by.bsu.n1jel.pc.assembler.dto.response.SpecificationTypeInfoResponseDto;
import by.bsu.n1jel.pc.assembler.entity.Specification;
import by.bsu.n1jel.pc.assembler.entity.SpecificationType;
import org.mapstruct.*;

import java.util.List;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = IGNORE)
public interface SpecificationMapper {

    @Mapping(target = "specificationTypeId", source = "type", qualifiedByName = "getSpecificationTypeId")
    SpecificationInfoResponseDto mapToResponse(Specification specification);
    
    List<SpecificationInfoResponseDto> mapToResponse(List<Specification> specifications);

    SpecificationTypeInfoResponseDto mapTypeToResponse(SpecificationType specification);

    List<SpecificationTypeInfoResponseDto> mapTypeToResponse(List<SpecificationType> specifications);

    Specification updateSpecification(@MappingTarget Specification specification, SpecificationEditRequestDto requestDto);

    SpecificationType updateSpecificationType(@MappingTarget SpecificationType specificationType, SpecificationTypeEditRequestDto requestDto);

    @Named("getSpecificationTypeId")
    default Long getSpecificationTypeId(SpecificationType specificationType) {
        return specificationType.getId();
    }
}
