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
import org.springframework.transaction.annotation.Transactional;

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

    private List<BuildPartition> findBuildPartitionsByIds(List<Long> ids) {
        return ids.stream().map(this::findBuildPartitionById).toList();
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
    @Transactional(readOnly = true)
    public List<BuildInfoResponseDto> getAllBuilds() {
        return buildMapper.mapToResponseDto(buildRepository.findAll());
    }

    @Override
    @Transactional
    public BuildInfoResponseDto createBuild(BuildCreateRequestDto requestDto) {
        Build createdBuild = Build.builder()
                .name(requestDto.name())
                .build();

        return buildMapper.mapToResponseDto(buildRepository.save(createdBuild));
    }

    @Override
    @Transactional(readOnly = true)
    public BuildInfoResponseDto getBuildById(Long buildId) {
        return buildMapper.mapToResponseDto(findBuildById(buildId));
    }

    @Override
    @Transactional
    public BuildInfoResponseDto editBuildInfo(BuildEditRequestDto requestDto) {
        Build buildFromDb = findBuildById(requestDto.buildId());
        buildMapper.updateBuild(buildFromDb, requestDto);
        return buildMapper.mapToResponseDto(buildRepository.save(buildFromDb));
    }

    @Override
    @Transactional
    public BuildInfoResponseDto deleteBuildById(Long buildId) {
        Build buildFromDb = findBuildById(buildId);
        BuildInfoResponseDto responseDto = buildMapper.mapToResponseDto(buildFromDb);
        buildRepository.delete(buildFromDb);
        return responseDto;
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
    @Transactional
    public BuildPartitionInfoResponseDto createBuildPartition(BuildPartitionCreateRequestDto requestDto) {

        BuildPartition createdBuildPartition = BuildPartition.builder()
                .component(findComponentById(requestDto.componentId()))
                .build(findBuildById(requestDto.buildId()))
                .quantity(requestDto.quantity())
                .build();

        return buildMapper.mapToPartitionResponseDto(partitionRepository.save(createdBuildPartition));
    }

    @Override
    @Transactional
    public BuildPartitionInfoResponseDto editBuildPartitionInfo(BuildPartitionEditRequestDto requestDto) {
        BuildPartition buildPartitionFromDb = findBuildPartitionById(requestDto.buildPartitionId());
        buildPartitionFromDb = buildMapper.updatePartition(buildPartitionFromDb, requestDto);
        editDifficultBuildPartitionInfo(buildPartitionFromDb, requestDto);

        return buildMapper.mapToPartitionResponseDto(partitionRepository.save(buildPartitionFromDb));
    }

    @Override
    @Transactional
    public BuildPartitionInfoResponseDto deleteBuildPartitionById(Long buildPartitionId) {
        BuildPartition buildPartitionFromDb = findBuildPartitionById(buildPartitionId);
        BuildPartitionInfoResponseDto responseDto = buildMapper.mapToPartitionResponseDto(buildPartitionFromDb);
        partitionRepository.delete(buildPartitionFromDb);
        return responseDto;
    }

    private void editDifficultBuildPartitionInfo(BuildPartition buildPartition, BuildPartitionEditRequestDto requestDto) {

        if (requestDto.buildPartitionId() != null) {
            buildPartition.setBuild(findBuildById(requestDto.buildPartitionId()));
        }

        if (requestDto.componentId() != null) {
            buildPartition.setComponent(findComponentById(requestDto.componentId()));
        }

    }
}
