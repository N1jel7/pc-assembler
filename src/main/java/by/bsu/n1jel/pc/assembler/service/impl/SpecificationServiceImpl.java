package by.bsu.n1jel.pc.assembler.service.impl;

import by.bsu.n1jel.pc.assembler.dto.request.SpecificationCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.SpecificationEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.SpecificationInfoResponseDto;
import by.bsu.n1jel.pc.assembler.entity.Specification;
import by.bsu.n1jel.pc.assembler.mapper.SpecificationMapper;
import by.bsu.n1jel.pc.assembler.repository.SpecificationRepository;
import by.bsu.n1jel.pc.assembler.service.api.SpecificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static by.bsu.n1jel.pc.assembler.exception.common.ResourceExceptionFactory.specificationNotFoundException;

@Service
@RequiredArgsConstructor
public class SpecificationServiceImpl implements SpecificationService {

    private final SpecificationRepository specsRepository;
    private final SpecificationMapper specsMapper;

    private Specification findSpecificationById(Long specificationId) {
        return specsRepository.findById(specificationId)
                .orElseThrow(
                        () -> specificationNotFoundException(specificationId)
                );
    }

    @Override
    public SpecificationInfoResponseDto getSpecificationById(Long specificationId) {
        return specsMapper.mapToResponse(findSpecificationById(specificationId));
    }

    @Override
    public List<SpecificationInfoResponseDto> getAllSpecifications() {
        return specsMapper.mapToResponse(specsRepository.findAll());
    }

    @Override
    @Transactional
    public SpecificationInfoResponseDto createSpecification(SpecificationCreateRequestDto requestDto) {
        Specification specification = Specification.builder()
                .name(requestDto.name())
                .build();

        if (!requestDto.description().isEmpty()) {
            specification.setDescription(requestDto.description());
        }

        return specsMapper.mapToResponse(specsRepository.save(specification));
    }

    @Override
    @Transactional
    public SpecificationInfoResponseDto editSpecification(SpecificationEditRequestDto requestDto) {
        Specification specificationFromDb = findSpecificationById(requestDto.id());
        specificationFromDb = specsMapper.updateSpecification(specificationFromDb, requestDto);
        return specsMapper.mapToResponse(specsRepository.save(specificationFromDb));
    }

    @Override
    @Transactional
    public SpecificationInfoResponseDto deleteSpecificationById(Long specificationId) {
        Specification specificationFromDb = findSpecificationById(specificationId);
        SpecificationInfoResponseDto responseDto = specsMapper.mapToResponse(specificationFromDb);
        specsRepository.delete(specificationFromDb);
        return responseDto;
    }
}
