package by.bsu.n1jel.pc.assembler.service.api;

import by.bsu.n1jel.pc.assembler.dto.request.create.BuildPartitionCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.BuildEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.create.BuildCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.BuildPartitionEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.search.BuildFilterRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.BuildInfoResponseDto;
import by.bsu.n1jel.pc.assembler.dto.response.BuildPartitionInfoResponseDto;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BuildService {

    // BUILDS

    List<BuildInfoResponseDto> getLatestBuilds();

    Page<BuildInfoResponseDto> findBuildsBySearchFilter(BuildFilterRequestDto requestDto, Integer pageNumber);

    Page<BuildInfoResponseDto> getAllBuildsByPage(Integer pageNumber);

    List<BuildInfoResponseDto> getAllBuilds();

    BuildInfoResponseDto getBuildById(Long buildId);

    BuildInfoResponseDto createBuild(BuildCreateRequestDto requestDto);

    BuildInfoResponseDto editBuildInfo(BuildEditRequestDto requestDto);

    BuildInfoResponseDto deleteBuildById(Long buildId);

    // BUILD PARTITIONS

    BuildPartitionInfoResponseDto createOrEditPartition(BuildPartitionCreateRequestDto requestDto);

    List<BuildPartitionInfoResponseDto> getAllBuildPartitions();

    BuildPartitionInfoResponseDto getBuildPartitionById(Long buildPartitionId);

    BuildPartitionInfoResponseDto createBuildPartition(BuildPartitionCreateRequestDto requestDto);

    BuildPartitionInfoResponseDto editBuildPartitionInfo(BuildPartitionEditRequestDto requestDto);

    BuildPartitionInfoResponseDto deleteBuildPartitionById(Long buildPartitionId);
}
