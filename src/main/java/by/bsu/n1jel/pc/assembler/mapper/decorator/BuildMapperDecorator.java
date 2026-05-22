package by.bsu.n1jel.pc.assembler.mapper.decorator;

import by.bsu.n1jel.pc.assembler.dto.response.BuildInfoResponseDto;
import by.bsu.n1jel.pc.assembler.entity.Build;
import by.bsu.n1jel.pc.assembler.mapper.BuildMapper;
import by.bsu.n1jel.pc.assembler.repository.BuildRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

public abstract class BuildMapperDecorator implements BuildMapper {

    @Autowired
    @Qualifier("delegate")
    private BuildMapper delegate;
    @Autowired
    private BuildRepository buildRepository;

    @Override
    public BuildInfoResponseDto mapToResponseDto(Build build) {
        BuildInfoResponseDto buildInfoResponseDto = delegate.mapToResponseDto(build);
        buildInfoResponseDto.setPrice(buildRepository.getBuildPrice(build.getId()).toPlainString());
        return buildInfoResponseDto;
    }

    @Override
    public List<BuildInfoResponseDto> mapToResponseDto(List<Build> builds) {
        return builds.stream().map(this::mapToResponseDto).toList();
    }

}
