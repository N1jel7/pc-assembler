package by.bsu.n1jel.pc.assembler.service.api;

import by.bsu.n1jel.pc.assembler.dto.request.ProducerCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.ProducerEditInfoRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.ProducerInfoResponseDto;

import java.util.List;

public interface ProducerService {

    List<ProducerInfoResponseDto> getAllProducers();

    ProducerInfoResponseDto createProducer(ProducerCreateRequestDto requestDto);

    ProducerInfoResponseDto getProducerById(Long producerId);

    ProducerInfoResponseDto editProducerInfo(ProducerEditInfoRequestDto requestDto);

    ProducerInfoResponseDto deleteProducerById(Long producerId);

}
