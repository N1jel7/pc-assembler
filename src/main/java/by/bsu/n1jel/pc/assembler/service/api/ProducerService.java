package by.bsu.n1jel.pc.assembler.service.api;

import by.bsu.n1jel.pc.assembler.dto.request.create.ProducerCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.ProducerEditInfoRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.search.ProducerFilterRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.ImageInfoResponseDto;
import by.bsu.n1jel.pc.assembler.dto.response.ProducerInfoResponseDto;
import org.springframework.web.multipart.MultipartFile;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProducerService {

    List<ProducerInfoResponseDto> getAllProducers();

    List<String> findAllProducerCountries();

    Page<ProducerInfoResponseDto> searchProducers(ProducerFilterRequestDto requestDto, Integer pageNumber);

    ProducerInfoResponseDto createProducer(ProducerCreateRequestDto requestDto);

    ProducerInfoResponseDto getProducerById(Long producerId);

    ProducerInfoResponseDto editProducerInfo(ProducerEditInfoRequestDto requestDto);

    ProducerInfoResponseDto deleteProducerById(Long producerId);

    // IMAGE — у производителя одно фото, оно же аватарка

    ProducerInfoResponseDto createProducer(ProducerCreateRequestDto requestDto, MultipartFile image);

    /**
     * Новый файл заменяет текущее фото; если файла нет и текущее фото не в keptImageIds — фото удаляется.
     */
    ProducerInfoResponseDto editProducerInfo(ProducerEditInfoRequestDto requestDto, List<Long> keptImageIds, MultipartFile image);

    ImageInfoResponseDto setImage(Long producerId, MultipartFile file);

    List<ProducerInfoResponseDto> getTopProducers();
}
