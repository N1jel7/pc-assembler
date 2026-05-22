package by.bsu.n1jel.pc.assembler.mapper;

import by.bsu.n1jel.pc.assembler.dto.request.edit.BuildEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.BuildPartitionEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.BuildInfoResponseDto;
import by.bsu.n1jel.pc.assembler.dto.response.BuildPartitionInfoResponseDto;
import by.bsu.n1jel.pc.assembler.entity.Build;
import by.bsu.n1jel.pc.assembler.entity.BuildPartition;
import by.bsu.n1jel.pc.assembler.entity.Component;
import by.bsu.n1jel.pc.assembler.mapper.decorator.BuildMapperDecorator;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
@DecoratedWith(BuildMapperDecorator.class)
public interface BuildMapper {

    @Mapping(target = "partitions", source = "buildPartitions")
    @Mapping(target = "buildId", source = "id")
    BuildInfoResponseDto mapToResponseDto(Build build);

    List<BuildInfoResponseDto> mapToResponseDto(List<Build> builds);

    @Mapping(target = "buildId", source = "build", qualifiedByName = "getBuildId")
    @Mapping(target = "componentId", source = "component", qualifiedByName = "getComponentId")
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
