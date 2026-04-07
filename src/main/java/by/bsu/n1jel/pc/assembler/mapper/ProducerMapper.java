package by.bsu.n1jel.pc.assembler.mapper;

import by.bsu.n1jel.pc.assembler.dto.request.ProducerEditInfoRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.ProducerInfoResponseDto;
import by.bsu.n1jel.pc.assembler.entity.Producer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import java.util.List;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = IGNORE)
public interface ProducerMapper {

    ProducerInfoResponseDto mapToResponseDto(Producer producer);

    List<ProducerInfoResponseDto> mapToResponseDto(List<Producer> producers);

    Producer updateProducer(@MappingTarget Producer producer, ProducerEditInfoRequestDto requestDto);
}
