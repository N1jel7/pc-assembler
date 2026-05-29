package by.bsu.n1jel.pc.assembler.mapper.decorator;

import by.bsu.n1jel.pc.assembler.dto.response.BuildInfoResponseDto;
import by.bsu.n1jel.pc.assembler.dto.response.BuildPartitionInfoResponseDto;
import by.bsu.n1jel.pc.assembler.entity.Build;
import by.bsu.n1jel.pc.assembler.entity.BuildPartition;
import by.bsu.n1jel.pc.assembler.entity.ComponentType;
import by.bsu.n1jel.pc.assembler.mapper.BuildMapper;
import by.bsu.n1jel.pc.assembler.repository.BuildRepository;
import by.bsu.n1jel.pc.assembler.repository.ComponentTypeRepository;
import by.bsu.n1jel.pc.assembler.service.utils.DefaultImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static by.bsu.n1jel.pc.assembler.exception.common.ResourceExceptionFactory.componentNotFoundException;
import static by.bsu.n1jel.pc.assembler.exception.common.ResourceExceptionFactory.componentTypeNotFoundException;

public abstract class BuildMapperDecorator implements BuildMapper {

    @Autowired
    @Qualifier("delegate")
    private BuildMapper delegate;

    @Autowired
    private BuildRepository buildRepository;

    @Autowired
    private ComponentTypeRepository componentTypeRepository;

    @Override
    public BuildInfoResponseDto mapToResponseDto(Build build) {

        BuildInfoResponseDto dto = delegate.mapToResponseDto(build);

        if (dto.getPartitions() != null) {
            List<BuildPartitionInfoResponseDto> decoratedPartitions = build.getBuildPartitions().stream()
                    .map(this::mapToPartitionResponseDto)
                    .toList();
            dto.setPartitions(decoratedPartitions);

            for (BuildPartitionInfoResponseDto partition : dto.getPartitions()) {
                if (partition.getComponentImage() == null) {
                    partition.setComponentImage(DefaultImageUtil.componentAvatar("SELECTED", partition.getComponentName()));
                }
            }
        }

        dto.setPrice(buildRepository.getBuildPrice(build.getId()).toPlainString());
        dto.setFilledSlots(buildRepository.getBuildFilledSlotsAmount(build.getId()));

        return dto;
    }

    @Override
    public List<BuildInfoResponseDto> mapToResponseDto(List<Build> builds) {
        return builds.stream().map(this::mapToResponseDto).toList();
    }


    @Override
    public BuildPartitionInfoResponseDto mapToPartitionResponseDto(BuildPartition buildPartition) {

        Long componentTypeId = buildPartition.getComponent().getComponentType().getId();

        ComponentType componentType = componentTypeRepository.findById(componentTypeId)
                .orElseThrow(
                        () -> componentTypeNotFoundException((componentTypeId))
                );

        BuildPartitionInfoResponseDto buildPartitionInfoResponseDto = delegate.mapToPartitionResponseDto(buildPartition);
        buildPartitionInfoResponseDto.setMinQuantity(componentType.getMinAmount());
        buildPartitionInfoResponseDto.setMaxQuantity(componentType.getMaxAmount());
        buildPartitionInfoResponseDto.setFixedQuantity((Objects.equals(componentType.getMaxAmount(), componentType.getMinAmount())));

        // BUILDPARTITION.COMPONENT.PRICE * BUILDPARTITION.QUANTITY
        BigDecimal subtotal = buildPartition.getComponent().getPrice().multiply(BigDecimal.valueOf(buildPartition.getQuantity()));
        buildPartitionInfoResponseDto.setSubtotal(subtotal);


        return buildPartitionInfoResponseDto;
    }

    @Override
    public List<BuildPartitionInfoResponseDto> mapToPartitionResponseDto(List<BuildPartition> buildPartitions) {
        return buildPartitions.stream().map(this::mapToPartitionResponseDto).toList();
    }


}
