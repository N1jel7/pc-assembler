package by.bsu.n1jel.pc.assembler.service.api;

import by.bsu.n1jel.pc.assembler.dto.request.ProducerCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.ProducerEditInfoRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.ProducerInfoResponseDto;

public interface ProducerService {

    ProducerInfoResponseDto createProducer(ProducerCreateRequestDto requestDto);

    ProducerInfoResponseDto getProducerById(Long producerId);

    ProducerInfoResponseDto editProducerInfo(ProducerEditInfoRequestDto requestDto);

    ProducerInfoResponseDto deleteProducerById(Long producerId);

}
