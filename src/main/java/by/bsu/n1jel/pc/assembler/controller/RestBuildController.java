package by.bsu.n1jel.pc.assembler.controller;

import by.bsu.n1jel.pc.assembler.dto.request.create.BuildCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.create.BuildPartitionCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.BuildEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.BuildPartitionEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.BuildInfoResponseDto;
import by.bsu.n1jel.pc.assembler.dto.response.BuildPartitionInfoResponseDto;
import by.bsu.n1jel.pc.assembler.service.api.BuildService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/builds")
@RequiredArgsConstructor
public class RestBuildController {

    private final BuildService buildService;

    // BUILDS ENDPOINTS

    @GetMapping
    List<BuildInfoResponseDto> getAllBuilds() {
        return buildService.getAllBuilds();
    }

    @GetMapping("/{buildId}")
    BuildInfoResponseDto getBuildById(@PathVariable Long buildId) {
        return buildService.getBuildById(buildId);
    }

    @PostMapping("/create")
    BuildInfoResponseDto createBuild(@RequestBody BuildCreateRequestDto requestDto) {
        return buildService.createBuild(requestDto);
    }

    @PatchMapping("/edit")
    BuildInfoResponseDto editBuild(@RequestBody BuildEditRequestDto requestDto) {
        return buildService.editBuildInfo(requestDto);
    }

    @DeleteMapping("/{buildId}")
    BuildInfoResponseDto deleteBuildById(@PathVariable Long buildId) {
        return buildService.deleteBuildById(buildId);
    }

    // BUILD PARTITIONS ENDPOINTS

    @GetMapping("/partitions")
    List<BuildPartitionInfoResponseDto> getAllBuildPartitions() {
        return buildService.getAllBuildPartitions();
    }

    @GetMapping("/partitions/{buildPartitionId}")
    BuildPartitionInfoResponseDto getBuildPartitionById(@PathVariable Long buildPartitionId) {
        return buildService.getBuildPartitionById(buildPartitionId);
    }

    @PostMapping("/partitions/create")
    BuildPartitionInfoResponseDto createBuildPartition(@RequestBody BuildPartitionCreateRequestDto requestDto) {
        return buildService.createBuildPartition(requestDto);
    }

    @PatchMapping("/partitions/edit")
    BuildPartitionInfoResponseDto editBuildPartition(@RequestBody BuildPartitionEditRequestDto requestDto) {
        return buildService.editBuildPartitionInfo(requestDto);
    }

    @DeleteMapping("/partitions/{buildPartitionId}")
    BuildPartitionInfoResponseDto deleteBuildPartitionById(@PathVariable Long buildPartitionId) {
        return buildService.deleteBuildPartitionById(buildPartitionId);
    }

}
