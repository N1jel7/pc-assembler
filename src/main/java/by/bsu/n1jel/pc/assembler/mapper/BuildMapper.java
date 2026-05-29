package by.bsu.n1jel.pc.assembler.mapper;

import by.bsu.n1jel.pc.assembler.dto.request.edit.BuildEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.BuildPartitionEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.BuildInfoResponseDto;
import by.bsu.n1jel.pc.assembler.dto.response.BuildPartitionInfoResponseDto;
import by.bsu.n1jel.pc.assembler.entity.Build;
import by.bsu.n1jel.pc.assembler.entity.BuildPartition;
import by.bsu.n1jel.pc.assembler.entity.Component;
import by.bsu.n1jel.pc.assembler.mapper.decorator.BuildMapperDecorator;
import by.bsu.n1jel.pc.assembler.service.utils.DefaultImageUtil;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
@DecoratedWith(BuildMapperDecorator.class)
public interface BuildMapper {

    @Mapping(target = "partitions", source = "buildPartitions")
    @Mapping(target = "buildId", source = "id")
    @Mapping(target = "avatar", source = "build", qualifiedByName = "getDefaultAvatar")
    BuildInfoResponseDto mapToResponseDto(Build build);

    List<BuildInfoResponseDto> mapToResponseDto(List<Build> builds);

    @Mapping(target = "buildId", source = "build", qualifiedByName = "getBuildId")
    @Mapping(target = "componentId", source = "component", qualifiedByName = "getComponentId")
    @Mapping(target = "componentName", source = "component", qualifiedByName = "getComponentName")
    @Mapping(target = "producerName", source = "component", qualifiedByName = "getProducerName")
    @Mapping(target = "componentPrice", source = "component", qualifiedByName = "getComponentPrice")
    @Mapping(target = "componentTypeId", source = "component", qualifiedByName = "getComponentTypeId")
    @Mapping(target = "componentTypeName", source = "component", qualifiedByName = "getComponentTypeName")
    @Mapping(target = "buildPartitionId", source = "id")
    BuildPartitionInfoResponseDto mapToPartitionResponseDto(BuildPartition buildPartition);

    List<BuildPartitionInfoResponseDto> mapToPartitionResponseDto(List<BuildPartition> buildPartitions);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "build", ignore = true)
    @Mapping(target = "component", ignore = true)
    @Mapping(source = "quantity", target = "quantity")
    BuildPartition updatePartition(@MappingTarget BuildPartition buildPartition, BuildPartitionEditRequestDto requestDto);

    @Mapping(target = "id", ignore = true)
    Build updateBuild(@MappingTarget Build build, BuildEditRequestDto requestDto);

    @Named("getComponentTypeName")
    default String getComponentTypeName(Component component) {
        return component.getComponentType().getName();
    }

    @Named("getComponentTypeId")
    default Long getComponentTypeId(Component component) {
        return component.getComponentType().getId();
    }


    @Named("getComponentPrice")
    default String getComponentPrice(Component component) {
        return component.getPrice().toPlainString();
    }

    @Named("getComponentName")
    default String getComponentName(Component component) {
        return component.getName();
    }

    @Named("getProducerName")
    default String getProducerName(Component component) {
        return component.getProducer().getName();
    }

    @Named("getDefaultAvatar")
    default String getDefaultAvatar(Build build) {
        return DefaultImageUtil.buildAvatar(build.getName());
    }

    @Named("getBuildId")
    default Long getBuildId(Build build) {
        if (build != null) {
            return build.getId();
        }
        return null;
    }

    @Named("getComponentId")
    default Long getComponentId(Component component) {
        if (component != null) {
            return component.getId();
        }
        return null;
    }
}
