package by.bsu.n1jel.pc.assembler.mapper;

import by.bsu.n1jel.pc.assembler.dto.request.edit.ComponentEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.ComponentTypeEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.ComponentTypeInfoResponseDto;
import by.bsu.n1jel.pc.assembler.dto.response.ComponentInfoResponseDto;
import by.bsu.n1jel.pc.assembler.dto.response.SpecificationInfoResponseDto;
import by.bsu.n1jel.pc.assembler.entity.*;
import by.bsu.n1jel.pc.assembler.dto.response.ImageInfoResponseDto;
import by.bsu.n1jel.pc.assembler.service.utils.DefaultImageUtil;
import by.bsu.n1jel.pc.assembler.service.utils.ImageUrlUtil;
import by.bsu.n1jel.pc.assembler.service.utils.SpecificationValueUtil;
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
    @Mapping(target = "images", source = "images", qualifiedByName = "getImages")
    ComponentInfoResponseDto mapToResponseDto(Component component);

    List<ComponentInfoResponseDto> mapToResponseDto(List<Component> components);

    @Mapping(target = "requiredSpecificationTypeIds", source = "requiredSpecifications", qualifiedByName = "getSpecificationTypeIds")
    ComponentTypeInfoResponseDto mapToTypeResponseDto(ComponentType componentType);

    List<ComponentTypeInfoResponseDto> mapToTypeResponseDto(List<ComponentType> componentTypes);

    @Mapping(target = "producer", ignore = true)
    @Mapping(target = "componentType", ignore = true)
    @Mapping(target = "specifications", ignore = true)
    @Mapping(target = "images", ignore = true)
    Component updateComponent(@MappingTarget Component component, ComponentEditRequestDto requestDto);

    @Mapping(target = "code", ignore = true)
    @Mapping(target = "requiredSpecifications", ignore = true)
    ComponentType updateComponentType(@MappingTarget ComponentType componentType, ComponentTypeEditRequestDto requestDto);

    @Named("getSpecificationInfoResponseDtoList")
    default List<SpecificationInfoResponseDto> getSpecificationInfoResponseDtoList(List<Specification> specifications) {
        List<SpecificationInfoResponseDto> specificationInfoResponseDtos = new ArrayList<>();

        if(specifications != null) {
            for (Specification spec : specifications) {
                SpecificationType type = spec.getType();
                specificationInfoResponseDtos.add(
                        new SpecificationInfoResponseDto(
                                spec.getId(),
                                type.getId(),
                                type.getName(),
                                type.getDescription(),
                                spec.getValue(),
                                type.getCode(),
                                type.getUnit(),
                                SpecificationValueUtil.display(type.getValueType(), spec.getValue(), type.getUnit())));
            }
            return specificationInfoResponseDtos;
        }
        return null;
    }

    @Named("getSpecificationTypeIds")
    default List<Long> getSpecificationTypeIds(List<SpecificationType> specificationTypes) {
        if (specificationTypes == null) {
            return List.of();
        }
        return specificationTypes.stream().map(SpecificationType::getId).toList();
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

    @Named("getImages")
    default List<ImageInfoResponseDto> getImages(List<Image> images) {
        return ImageUrlUtil.toInfo(images);
    }

    @Named("getDefaultAvatar")
    default String getDefaultAvatar(Component component) {
        return DefaultImageUtil.componentAvatar(component.getComponentType().getName(), component.getName());
    }
}
