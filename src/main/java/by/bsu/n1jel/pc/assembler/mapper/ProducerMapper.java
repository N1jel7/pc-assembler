package by.bsu.n1jel.pc.assembler.mapper;

import by.bsu.n1jel.pc.assembler.dto.request.edit.ProducerEditInfoRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.ImageInfoResponseDto;
import by.bsu.n1jel.pc.assembler.dto.response.ProducerInfoResponseDto;
import by.bsu.n1jel.pc.assembler.entity.Image;
import by.bsu.n1jel.pc.assembler.entity.Producer;
import by.bsu.n1jel.pc.assembler.service.utils.DefaultImageUtil;
import by.bsu.n1jel.pc.assembler.service.utils.ImageUrlUtil;
import org.mapstruct.*;

import java.util.List;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = IGNORE)
public interface ProducerMapper {

    @Mapping(target = "avatar", source = "producer", qualifiedByName = "getAvatar")
    @Mapping(target = "image", source = "image", qualifiedByName = "getImage")
    ProducerInfoResponseDto mapToResponseDto(Producer producer);

    List<ProducerInfoResponseDto> mapToResponseDto(List<Producer> producers);

    @Mapping(target = "image", ignore = true)
    Producer updateProducer(@MappingTarget Producer producer, ProducerEditInfoRequestDto requestDto);

    @Named("getAvatar")
    default String getAvatar(Producer producer) {
        if (producer.getImage() != null) {
            return ImageUrlUtil.url(producer.getImage().getId());
        }
        return DefaultImageUtil.producerAvatar(producer.getName(), producer.getCountry());
    }

    @Named("getImage")
    default ImageInfoResponseDto getImage(Image image) {
        return image == null ? null : ImageUrlUtil.toInfo(image);
    }
}
