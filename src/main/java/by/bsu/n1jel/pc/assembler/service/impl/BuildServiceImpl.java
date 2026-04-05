package by.bsu.n1jel.pc.assembler.service.impl;

import by.bsu.n1jel.pc.assembler.dto.request.BuildCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.BuildEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.BuildInfoResponseDto;
import by.bsu.n1jel.pc.assembler.service.api.BuildService;
import org.springframework.stereotype.Service;

@Service
public class BuildServiceImpl implements BuildService {
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
}
