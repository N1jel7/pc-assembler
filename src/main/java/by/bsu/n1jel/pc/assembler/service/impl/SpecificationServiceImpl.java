package by.bsu.n1jel.pc.assembler.service.impl;

import by.bsu.n1jel.pc.assembler.dto.request.SpecificationTypeCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.SpecificationTypeEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.SpecificationTypeInfoResponseDto;
import by.bsu.n1jel.pc.assembler.entity.SpecificationType;
import by.bsu.n1jel.pc.assembler.mapper.SpecificationMapper;
import by.bsu.n1jel.pc.assembler.repository.SpecificationTypeRepository;
import by.bsu.n1jel.pc.assembler.service.api.SpecificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static by.bsu.n1jel.pc.assembler.exception.common.ResourceExceptionFactory.specificationTypeNotFoundException;

@Service
@RequiredArgsConstructor
public class SpecificationServiceImpl implements SpecificationService {

    private final SpecificationTypeRepository specsTypeRepository;
    private final SpecificationMapper specsMapper;

    private SpecificationType findSpecificationTypeById(Long specificationId) {
        return specsTypeRepository.findById(specificationId)
                .orElseThrow(
                        () -> specificationTypeNotFoundException(specificationId)
                );
    }

    @Override
    public SpecificationTypeInfoResponseDto getSpecificationTypeById(Long specificationId) {
        return specsMapper.mapTypeToResponse(findSpecificationTypeById(specificationId));
    }

    @Override
    public List<SpecificationTypeInfoResponseDto> getAllSpecificationTypes() {
        return specsMapper.mapTypeToResponse(specsTypeRepository.findAll());
    }

    @Override
    @Transactional
    public SpecificationTypeInfoResponseDto createSpecificationType(SpecificationTypeCreateRequestDto requestDto) {
        SpecificationType specification = SpecificationType.builder()
                .name(requestDto.name())
                .build();

        if (!requestDto.description().isEmpty()) {
            specification.setDescription(requestDto.description());
        }

        return specsMapper.mapTypeToResponse(specsTypeRepository.save(specification));
    }

    @Override
    @Transactional
    public SpecificationTypeInfoResponseDto editSpecificationType(SpecificationTypeEditRequestDto requestDto) {
        SpecificationType specificationFromDb = findSpecificationTypeById(requestDto.id());
        specificationFromDb = specsMapper.updateSpecificationType(specificationFromDb, requestDto);
        return specsMapper.mapTypeToResponse(specsTypeRepository.save(specificationFromDb));
    }

    @Override
    @Transactional
    public SpecificationTypeInfoResponseDto deleteSpecificationTypeById(Long specificationId) {
        SpecificationType specificationFromDb = findSpecificationTypeById(specificationId);
        SpecificationTypeInfoResponseDto responseDto = specsMapper.mapTypeToResponse(specificationFromDb);
        specsTypeRepository.delete(specificationFromDb);
        return responseDto;
    }
}
