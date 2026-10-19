package by.bsu.n1jel.pc.assembler.service.impl;

import by.bsu.n1jel.pc.assembler.dao.SearchDao;
import by.bsu.n1jel.pc.assembler.dto.request.create.ProducerCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.ProducerEditInfoRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.search.ProducerFilterRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.ImageInfoResponseDto;
import by.bsu.n1jel.pc.assembler.dto.response.ProducerInfoResponseDto;
import by.bsu.n1jel.pc.assembler.entity.Image;
import by.bsu.n1jel.pc.assembler.entity.Producer;
import by.bsu.n1jel.pc.assembler.entity.enums.ObjectType;
import by.bsu.n1jel.pc.assembler.mapper.ProducerMapper;
import by.bsu.n1jel.pc.assembler.repository.ProducerRepository;
import by.bsu.n1jel.pc.assembler.service.api.ImageService;
import by.bsu.n1jel.pc.assembler.service.api.ProducerService;
import by.bsu.n1jel.pc.assembler.service.utils.ImageUrlUtil;
import by.bsu.n1jel.pc.assembler.service.utils.OverallUtil;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static by.bsu.n1jel.pc.assembler.exception.common.ResourceExceptionFactory.producerNotFoundException;

@Service
@RequiredArgsConstructor
public class ProducerServiceImpl implements ProducerService {

    private final ProducerRepository producerRepository;
    private final ProducerMapper producerMapper;
    private final SearchDao searchDao;
    private final ImageService imageService;

    private Producer findProducerById(Long producerId) {
        return producerRepository.findById(producerId)
                .orElseThrow(
                        () -> producerNotFoundException(producerId)
                );
    }

    private Image replaceImage(Producer producer, MultipartFile file) {
        Image newImage = imageService.store(file, ObjectType.PRODUCER, producer.getId());
        removeImage(producer);
        producer.setImage(newImage);
        return newImage;
    }

    private void removeImage(Producer producer) {
        if (producer.getImage() != null) {
            imageService.removeFromStorage(List.of(producer.getImage()));
            // строка в images удалится через orphanRemoval
            producer.setImage(null);
        }
    }

    @Override
    @Transactional
    public ProducerInfoResponseDto createProducer(ProducerCreateRequestDto requestDto, MultipartFile image) {
        Producer producer = findProducerById(createProducer(requestDto).getId());
        if (image != null && !image.isEmpty()) {
            replaceImage(producer, image);
        }
        return producerMapper.mapToResponseDto(producer);
    }

    @Override
    @Transactional
    public ProducerInfoResponseDto editProducerInfo(ProducerEditInfoRequestDto requestDto, List<Long> keptImageIds, MultipartFile image) {
        editProducerInfo(requestDto);
        Producer producer = findProducerById(requestDto.id());

        if (image != null && !image.isEmpty()) {
            replaceImage(producer, image);
        } else if (producer.getImage() != null
                && (keptImageIds == null || !keptImageIds.contains(producer.getImage().getId()))) {
            removeImage(producer);
        }

        return producerMapper.mapToResponseDto(producer);
    }

    @Override
    @Transactional
    public ImageInfoResponseDto setImage(Long producerId, MultipartFile file) {
        return ImageUrlUtil.toInfo(replaceImage(findProducerById(producerId), file));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProducerInfoResponseDto> getAllProducers() {
        return producerMapper.mapToResponseDto(producerRepository.findAll());
    }

    @Override
    public List<String> findAllProducerCountries() {
        return producerRepository.findAllProducerCountries();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProducerInfoResponseDto> searchProducers(ProducerFilterRequestDto requestDto, Integer pageNumber) {
        Page<Producer> producersPage = searchDao.searchProducers(requestDto, PageRequest.of(--pageNumber, OverallUtil.getPageSize()));
        return producersPage.map(producerMapper::mapToResponseDto);
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
    @Transactional(readOnly = true)
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
        if (producerFromDb.getImage() != null) {
            imageService.removeFromStorage(List.of(producerFromDb.getImage()));
        }
        producerRepository.delete(producerFromDb);
        return responseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProducerInfoResponseDto> getTopProducers() {
        return producerMapper.mapToResponseDto(producerRepository.findTopProducers(OverallUtil.getLatestObjectSize()));
    }
}
