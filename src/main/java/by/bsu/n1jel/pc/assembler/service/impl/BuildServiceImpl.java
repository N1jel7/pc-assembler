package by.bsu.n1jel.pc.assembler.service.impl;

import by.bsu.n1jel.pc.assembler.dao.SearchDao;
import by.bsu.n1jel.pc.assembler.dto.request.create.BuildCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.create.BuildPartitionCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.BuildEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.BuildPartitionEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.search.BuildFilterRequestDto;
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
import by.bsu.n1jel.pc.assembler.service.utils.OverallUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final SearchDao searchDao;

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
    @Transactional(readOnly = true)
    public List<BuildInfoResponseDto> getLatestBuilds() {
        return buildMapper.mapToResponseDto(buildRepository.findLatestBuilds(OverallUtil.getLatestObjectSize()));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BuildInfoResponseDto> findBuildsBySearchFilter(BuildFilterRequestDto requestDto, Integer pageNumber) {
        Page<Build> buildPage = searchDao.searchBuilds(requestDto, PageRequest.of(--pageNumber, OverallUtil.getPageSize()));
        return buildPage.map(buildMapper::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BuildInfoResponseDto> getAllBuildsByPage(Integer pageNumber) {
        Page<Build> buildPage = buildRepository.findAll(PageRequest.of(--pageNumber, OverallUtil.getPageSize()));
        return buildPage.map(buildMapper::mapToResponseDto);
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
                .name(requestDto.getName())
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
        Build buildFromDb = findBuildById(requestDto.getBuildId());
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
    @Transactional
    public BuildPartitionInfoResponseDto createOrEditPartition(BuildPartitionCreateRequestDto requestDto) {

        Build build = findBuildById(requestDto.getBuildId());

        Component component = findComponentById(requestDto.getComponentId());
        Long findableComponentTypeId = component.getComponentType().getId();

        for(BuildPartition buildPartition : build.getBuildPartitions()) {
            if(buildPartition.getComponent().getComponentType().getId().equals(findableComponentTypeId)) {
                return editBuildPartitionInfo(new BuildPartitionEditRequestDto(buildPartition.getId(), requestDto.getComponentId(), requestDto.getQuantity()));
            }
        }

        return createBuildPartition(requestDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BuildPartitionInfoResponseDto> getAllBuildPartitions() {
        return buildMapper.mapToPartitionResponseDto(partitionRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public BuildPartitionInfoResponseDto getBuildPartitionById(Long buildPartitionId) {
        return buildMapper.mapToPartitionResponseDto(findBuildPartitionById(buildPartitionId));
    }

    @Override
    @Transactional
    public BuildPartitionInfoResponseDto createBuildPartition(BuildPartitionCreateRequestDto requestDto) {

        BuildPartition createdBuildPartition = BuildPartition.builder()
                .component(findComponentById(requestDto.getComponentId()))
                .build(findBuildById(requestDto.getBuildId()))
                .quantity(requestDto.getQuantity())
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

        if (requestDto.componentId() != null) {
            buildPartition.setComponent(findComponentById(requestDto.componentId()));
        }

    }
}
