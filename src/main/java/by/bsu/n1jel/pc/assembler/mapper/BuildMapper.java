package by.bsu.n1jel.pc.assembler.mapper;

import by.bsu.n1jel.pc.assembler.dto.request.edit.BuildEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.BuildPartitionEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.BuildInfoResponseDto;
import by.bsu.n1jel.pc.assembler.dto.response.BuildPartitionInfoResponseDto;
import by.bsu.n1jel.pc.assembler.entity.Build;
import by.bsu.n1jel.pc.assembler.entity.BuildPartition;
import by.bsu.n1jel.pc.assembler.entity.Component;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BuildMapper {

    BuildInfoResponseDto mapToResponseDto(Build build);

    List<BuildInfoResponseDto> mapToResponseDto(List<Build> builds);

    @Mapping(target = "buildId", source = "build", qualifiedByName = "getBuildId")
    @Mapping(target = "componentId", source = "component", qualifiedByName = "getComponentId")
    @Mapping(target = "partitionId", source = "id")
    BuildPartitionInfoResponseDto mapToPartitionResponseDto(BuildPartition buildPartition);

    List<BuildPartitionInfoResponseDto> mapToPartitionResponseDto(List<BuildPartition> buildPartitions);

    Build update(@MappingTarget Build build, BuildEditRequestDto requestDto);

    @Mapping(target = "build", ignore = true)
    @Mapping(target = "component", ignore = true)
    @Mapping(source = "quantity", target = "quantity")
    BuildPartition updatePartition(@MappingTarget BuildPartition buildPartition, BuildPartitionEditRequestDto requestDto);

    @Named("getBuildId")
    default Long getBuildId(Build build) {
        if(build != null) {
            return build.getId();
        }
        return null;
    }

    @Named("getComponentId")
    default Long getComponentId(Component component) {
        if(component != null) {
            return component.getId();
        }
        return null;
    }
}
