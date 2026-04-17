package by.bsu.n1jel.pc.assembler.mapper;

import by.bsu.n1jel.pc.assembler.dto.request.ComponentEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.ComponentTypeEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.ComponentTypeInfoResponseDto;
import by.bsu.n1jel.pc.assembler.dto.response.ComponentInfoResponseDto;
import by.bsu.n1jel.pc.assembler.dto.response.SpecificationInfoResponseDto;
import by.bsu.n1jel.pc.assembler.entity.Component;
import by.bsu.n1jel.pc.assembler.entity.ComponentType;
import by.bsu.n1jel.pc.assembler.entity.Producer;
import by.bsu.n1jel.pc.assembler.entity.Specification;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = IGNORE)
public interface ComponentMapper {

    @Mapping(target = "producer", source = "producer", qualifiedByName = "getProducerId")
    @Mapping(target = "componentType", source = "componentType", qualifiedByName = "getComponentTypeId")
    @Mapping(target = "specifications", source = "specifications", qualifiedByName = "getSpecificationInfoResponseDtoList")
    ComponentInfoResponseDto mapToResponseDto(Component component);

    List<ComponentInfoResponseDto> mapToResponseDto(List<Component> components);

    @Mapping(target = "componentParentType", source = "componentType", qualifiedByName = "getComponentParentTypeId")
    ComponentTypeInfoResponseDto mapToTypeResponseDto(ComponentType componentType);

    List<ComponentTypeInfoResponseDto> mapToTypeResponseDto(List<ComponentType> componentTypes);

    @Mapping(target = "producer", ignore = true)
    @Mapping(target = "componentType", ignore = true)
    @Mapping(target = "specifications", ignore = true)
    Component updateComponent(@MappingTarget Component component, ComponentEditRequestDto requestDto);

    @Mapping(target = "componentParentType", ignore = true)
    ComponentType updateComponentType(@MappingTarget ComponentType componentType, ComponentTypeEditRequestDto requestDto);

    @Named("getSpecificationInfoResponseDtoList")
    default List<SpecificationInfoResponseDto> getSpecificationInfoResponseDtoList(List<Specification> specifications) {
        List<SpecificationInfoResponseDto> specificationInfoResponseDtos = new ArrayList<>();

        if(specifications != null) {
            for (Specification spec : specifications) {
                specificationInfoResponseDtos.add(
                        new SpecificationInfoResponseDto(
                                spec.getId(),
                                spec.getType().getName(),
                                spec.getType().getDescription(),
                                spec.getValue()));
            }
            return specificationInfoResponseDtos;
        }
        return null;
    }

    @Named("getProducerId")
    default Long getProducerId(Producer producer) {
        return producer.getId();
    }

    @Named("getComponentTypeId")
    default Long getComponentTypeId(ComponentType componentType) {
        return componentType.getId();
    }

    @Named("getComponentParentTypeId")
    default Long getComponentParentTypeId(ComponentType componentType) {
        if (componentType.getComponentParentType() != null) {
            return componentType.getComponentParentType().getId();
        }
        return null;
    }
}
