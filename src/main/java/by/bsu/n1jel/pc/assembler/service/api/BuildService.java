package by.bsu.n1jel.pc.assembler.service.api;

import by.bsu.n1jel.pc.assembler.dto.request.BuildEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.BuildCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.BuildInfoResponseDto;

public interface BuildService {

    BuildInfoResponseDto createBuild(BuildCreateRequestDto requestDto);

    BuildInfoResponseDto getBuildById(Long buildId);

    BuildInfoResponseDto editBuildInfo(BuildEditRequestDto requestDto);

    BuildInfoResponseDto deleteBuildById(Long buildId);

}
