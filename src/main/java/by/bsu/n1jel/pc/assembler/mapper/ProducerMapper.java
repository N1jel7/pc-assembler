package by.bsu.n1jel.pc.assembler.mapper;

import by.bsu.n1jel.pc.assembler.dto.request.edit.ProducerEditInfoRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.ProducerInfoResponseDto;
import by.bsu.n1jel.pc.assembler.entity.Build;
import by.bsu.n1jel.pc.assembler.entity.Producer;
import by.bsu.n1jel.pc.assembler.service.utils.DefaultImageUtil;
import org.mapstruct.*;

import java.util.List;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = IGNORE)
public interface ProducerMapper {

    @Mapping(target = "avatar", source = "producer", qualifiedByName = "getDefaultAvatar")
    ProducerInfoResponseDto mapToResponseDto(Producer producer);

    List<ProducerInfoResponseDto> mapToResponseDto(List<Producer> producers);

    Producer updateProducer(@MappingTarget Producer producer, ProducerEditInfoRequestDto requestDto);

    @Named("getDefaultAvatar")
    default String getDefaultAvatar(Producer producer) {
        return DefaultImageUtil.producerAvatar(producer.getName(), producer.getCountry());
    }
}
