package by.bsu.n1jel.pc.assembler.mapper;

import by.bsu.n1jel.pc.assembler.dto.request.edit.ComponentEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.ComponentTypeEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.ComponentTypeInfoResponseDto;
import by.bsu.n1jel.pc.assembler.dto.response.ComponentInfoResponseDto;
import by.bsu.n1jel.pc.assembler.dto.response.SpecificationInfoResponseDto;
import by.bsu.n1jel.pc.assembler.entity.*;
import by.bsu.n1jel.pc.assembler.service.utils.DefaultImageUtil;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = IGNORE)
public interface ComponentMapper {

    @Mapping(target = "producerId", source = "producer", qualifiedByName = "getProducerId")
    @Mapping(target = "producerName", source = "producer", qualifiedByName = "getProducerName")
    @Mapping(target = "componentTypeId", source = "componentType", qualifiedByName = "getComponentTypeId")
    @Mapping(target = "componentTypeName", source = "componentType", qualifiedByName = "getComponentTypeName")
    @Mapping(target = "specifications", source = "specifications", qualifiedByName = "getSpecificationInfoResponseDtoList")
    @Mapping(target = "avatar", source = "component", qualifiedByName = "getDefaultAvatar")
    ComponentInfoResponseDto mapToResponseDto(Component component);

    List<ComponentInfoResponseDto> mapToResponseDto(List<Component> components);

    ComponentTypeInfoResponseDto mapToTypeResponseDto(ComponentType componentType);

    List<ComponentTypeInfoResponseDto> mapToTypeResponseDto(List<ComponentType> componentTypes);

    @Mapping(target = "producer", ignore = true)
    @Mapping(target = "componentType", ignore = true)
    @Mapping(target = "specifications", ignore = true)
    Component updateComponent(@MappingTarget Component component, ComponentEditRequestDto requestDto);

    ComponentType updateComponentType(@MappingTarget ComponentType componentType, ComponentTypeEditRequestDto requestDto);

    @Named("getSpecificationInfoResponseDtoList")
    default List<SpecificationInfoResponseDto> getSpecificationInfoResponseDtoList(List<Specification> specifications) {
        List<SpecificationInfoResponseDto> specificationInfoResponseDtos = new ArrayList<>();

        if(specifications != null) {
            for (Specification spec : specifications) {
                specificationInfoResponseDtos.add(
                        new SpecificationInfoResponseDto(
                                spec.getId(),
                                spec.getType().getId(),
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

    @Named("getProducerName")
    default String getProducerName(Producer producer) {
        return producer.getName();
    }

    @Named("getComponentTypeId")
    default Long getComponentTypeId(ComponentType componentType) {
        return componentType.getId();
    }

    @Named("getComponentTypeName")
    default String getComponentTypeName(ComponentType componentType) {
        return componentType.getName();
    }

    @Named("getDefaultAvatar")
    default String getDefaultAvatar(Component component) {
        return DefaultImageUtil.componentAvatar(component.getComponentType().getName(), component.getName());
    }
}
