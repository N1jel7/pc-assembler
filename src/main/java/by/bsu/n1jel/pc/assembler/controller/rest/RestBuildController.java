package by.bsu.n1jel.pc.assembler.controller.rest;

import by.bsu.n1jel.pc.assembler.dto.request.create.BuildCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.create.BuildPartitionCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.BuildEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.BuildPartitionEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.BuildInfoResponseDto;
import by.bsu.n1jel.pc.assembler.dto.response.BuildPartitionInfoResponseDto;
import by.bsu.n1jel.pc.assembler.service.api.BuildService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Build&Partition", description = "Build and build partition rest controller")
@RestController
@RequestMapping("api/v1/builds")
@RequiredArgsConstructor
public class RestBuildController {

    private final BuildService buildService;

    // BUILDS ENDPOINTS

    @Operation(
            summary = "Get all builds",
            description = "Return all existed builds from database"
    )
    @GetMapping
    List<BuildInfoResponseDto> getAllBuilds() {
        return buildService.getAllBuilds();
    }

    @Operation(
            summary = "Get build by id",
            description = "Return build by it's id"
    )
    @GetMapping("/{buildId}")
    BuildInfoResponseDto getBuildById(@PathVariable Long buildId) {
        return buildService.getBuildById(buildId);
    }

    @Operation(
            summary = "Create build",
            description = "Creating new build from request"
    )
    @PostMapping("/create")
    BuildInfoResponseDto createBuild(@RequestBody BuildCreateRequestDto requestDto) {
        return buildService.createBuild(requestDto);
    }

    @Operation(
            summary = "Edit build",
            description = "Editing existing parameters of build"
    )
    @PatchMapping("/edit")
    BuildInfoResponseDto editBuild(@RequestBody BuildEditRequestDto requestDto) {
        return buildService.editBuildInfo(requestDto);
    }

    @Operation(
            summary = "Delete build",
            description = "Deleting existing build by id"
    )
    @DeleteMapping("/{buildId}")
    BuildInfoResponseDto deleteBuildById(@PathVariable Long buildId) {
        return buildService.deleteBuildById(buildId);
    }

    // BUILD PARTITIONS ENDPOINTS

    @Operation(
            summary = "Get all builds",
            description = "Return all existed partitions from database"
    )
    @GetMapping("/partitions")
    List<BuildPartitionInfoResponseDto> getAllBuildPartitions() {
        return buildService.getAllBuildPartitions();
    }

    @Operation(
            summary = "Get partition by id",
            description = "Return build partition by it's id"
    )
    @GetMapping("/partitions/{buildPartitionId}")
    BuildPartitionInfoResponseDto getBuildPartitionById(@PathVariable Long buildPartitionId) {
        return buildService.getBuildPartitionById(buildPartitionId);
    }

    @Operation(
            summary = "Create partition",
            description = "Creating new build partition from request"
    )
    @PostMapping("/partitions/create")
    BuildPartitionInfoResponseDto createBuildPartition(@RequestBody BuildPartitionCreateRequestDto requestDto) {
        return buildService.createBuildPartition(requestDto);
    }

    @Operation(
            summary = "Edit partition",
            description = "Editing existing parameters of build partition"
    )
    @PatchMapping("/partitions/edit")
    BuildPartitionInfoResponseDto editBuildPartition(@RequestBody BuildPartitionEditRequestDto requestDto) {
        return buildService.editBuildPartitionInfo(requestDto);
    }

    @Operation(
            summary = "Delete partition",
            description = "Deleting existing build partition by id"
    )
    @DeleteMapping("/partitions/{buildPartitionId}")
    BuildPartitionInfoResponseDto deleteBuildPartitionById(@PathVariable Long buildPartitionId) {
        return buildService.deleteBuildPartitionById(buildPartitionId);
    }

}
