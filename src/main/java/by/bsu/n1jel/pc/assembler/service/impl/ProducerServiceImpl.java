package by.bsu.n1jel.pc.assembler.service.impl;

import by.bsu.n1jel.pc.assembler.dto.request.create.ProducerCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.ProducerEditInfoRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.ProducerInfoResponseDto;
import by.bsu.n1jel.pc.assembler.entity.Producer;
import by.bsu.n1jel.pc.assembler.mapper.ProducerMapper;
import by.bsu.n1jel.pc.assembler.repository.ProducerRepository;
import by.bsu.n1jel.pc.assembler.service.api.ProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static by.bsu.n1jel.pc.assembler.exception.common.ResourceExceptionFactory.producerNotFoundException;

@Service
@RequiredArgsConstructor
public class ProducerServiceImpl implements ProducerService {

    private final ProducerRepository producerRepository;
    private final ProducerMapper producerMapper;

    private Producer findProducerById(Long producerId) {
        return producerRepository.findById(producerId)
                .orElseThrow(
                        () -> producerNotFoundException(producerId)
                );
    }

    @Override
    public List<ProducerInfoResponseDto> getAllProducers() {
        return producerMapper.mapToResponseDto(producerRepository.findAll());
    }

    @Override
    @Transactional
    public ProducerInfoResponseDto createProducer(ProducerCreateRequestDto requestDto) {
        Producer createdProducer = Producer.builder()
                .name(requestDto.name())
                .country(requestDto.country())
                .build();
        return producerMapper.mapToResponseDto(producerRepository.save(createdProducer));
    }

    @Override
    public ProducerInfoResponseDto getProducerById(Long producerId) {
        return producerMapper.mapToResponseDto(findProducerById(producerId));
    }

    @Override
    @Transactional
    public ProducerInfoResponseDto editProducerInfo(ProducerEditInfoRequestDto requestDto) {
        Producer producerFromDb = findProducerById(requestDto.id());
        producerFromDb = producerMapper.updateProducer(producerFromDb, requestDto);
        return producerMapper.mapToResponseDto(producerRepository.save(producerFromDb));
    }

    @Override
    @Transactional
    public ProducerInfoResponseDto deleteProducerById(Long producerId) {
        Producer producerFromDb = findProducerById(producerId);
        ProducerInfoResponseDto responseDto = producerMapper.mapToResponseDto(producerFromDb);
        producerRepository.delete(producerFromDb);
        return responseDto;
    }
}
