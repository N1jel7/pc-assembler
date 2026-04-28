package by.bsu.n1jel.pc.assembler.service.impl;

import by.bsu.n1jel.pc.assembler.dto.request.create.BuildCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.create.BuildPartitionCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.BuildEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.BuildPartitionEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.BuildInfoResponseDto;
import by.bsu.n1jel.pc.assembler.dto.response.BuildPartitionInfoResponseDto;
import by.bsu.n1jel.pc.assembler.entity.Build;
import by.bsu.n1jel.pc.assembler.entity.BuildPartition;
import by.bsu.n1jel.pc.assembler.entity.Component;
import by.bsu.n1jel.pc.assembler.mapper.BuildMapper;
import by.bsu.n1jel.pc.assembler.repository.BuildPartitionRepository;
import by.bsu.n1jel.pc.assembler.repository.BuildRepository;
import by.bsu.n1jel.pc.assembler.repository.ComponentRepository;
import by.bsu.n1jel.pc.assembler.service.api.BuildService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static by.bsu.n1jel.pc.assembler.exception.common.ResourceExceptionFactory.*;

@Service
@RequiredArgsConstructor
public class BuildServiceImpl implements BuildService {

    private final BuildMapper buildMapper;
    private final BuildRepository buildRepository;
    private final BuildPartitionRepository partitionRepository;
    private final ComponentRepository componentRepository;

    private BuildPartition findBuildPartitionById(Long buildPartitionId) {
        return partitionRepository.findById(buildPartitionId)
                .orElseThrow(
                        () -> buildPartitionNotFoundException(buildPartitionId)
                );
    }

    private Build findBuildById(Long buildId) {
        return buildRepository.findById(buildId)
                .orElseThrow(
                        () -> buildNotFoundException(buildId)
                );
    }

    private Component findComponentById(Long componentId) {
        return componentRepository.findById(componentId)
                .orElseThrow(
                        () -> componentNotFoundException(componentId)
                );
    }

    @Override
    public List<BuildInfoResponseDto> getAllBuilds() {
        return buildMapper.mapToResponseDto(buildRepository.findAll());
    }

    @Override
    public BuildInfoResponseDto createBuild(BuildCreateRequestDto requestDto) {
        return null;
    }

    @Override
    public BuildInfoResponseDto getBuildById(Long buildId) {
        return null;
    }

    @Override
    public BuildInfoResponseDto editBuildInfo(BuildEditRequestDto requestDto) {
        return null;
    }

    @Override
    public BuildInfoResponseDto deleteBuildById(Long buildId) {
        return null;
    }

    @Override
    public List<BuildPartitionInfoResponseDto> getAllBuildPartitions() {
        return buildMapper.mapToPartitionResponseDto(partitionRepository.findAll());
    }

    @Override
    public BuildPartitionInfoResponseDto getBuildPartitionById(Long buildPartitionId) {
        return buildMapper.mapToPartitionResponseDto(findBuildPartitionById(buildPartitionId));
    }

    @Override
    public BuildPartitionInfoResponseDto createBuildPartition(BuildPartitionCreateRequestDto requestDto) {

        BuildPartition createdBuildPartition = BuildPartition.builder()
                .component(findComponentById(requestDto.componentId()))
                .quantity(requestDto.quantity())
                .build();

        return buildMapper.mapToPartitionResponseDto(partitionRepository.save(createdBuildPartition));
    }

    @Override
    public BuildPartitionInfoResponseDto editBuildPartitionInfo(BuildPartitionEditRequestDto requestDto) {
        BuildPartition buildPartitionFromDb = findBuildPartitionById(requestDto.buildId());
        buildPartitionFromDb = buildMapper.updatePartition(buildPartitionFromDb, requestDto);
        editDifficultData(buildPartitionFromDb, requestDto);

        return buildMapper.mapToPartitionResponseDto(partitionRepository.save(buildPartitionFromDb));
    }

    private void editDifficultData(BuildPartition buildPartition, BuildPartitionEditRequestDto requestDto) {

        if (requestDto.buildId() != null) {
            buildPartition.setBuild(findBuildById(requestDto.buildId()));
        }

        if (requestDto.componentId() != null) {
            buildPartition.setComponent(findComponentById(requestDto.componentId()));
        }

    }

    @Override
    public BuildPartitionInfoResponseDto deleteBuildPartitionById(Long buildPartitionId) {
        BuildPartition buildPartitionFromDb = findBuildPartitionById(buildPartitionId);
        BuildPartitionInfoResponseDto responseDto = buildMapper.mapToPartitionResponseDto(buildPartitionFromDb);
        partitionRepository.delete(buildPartitionFromDb);
        return responseDto;
    }
}
