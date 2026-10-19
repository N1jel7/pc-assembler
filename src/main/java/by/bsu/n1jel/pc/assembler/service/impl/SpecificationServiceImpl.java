package by.bsu.n1jel.pc.assembler.service.impl;

import by.bsu.n1jel.pc.assembler.dao.SearchDao;
import by.bsu.n1jel.pc.assembler.dto.request.create.SpecificationTypeCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.SpecificationTypeEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.search.SpecificationTypeFilterRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.SpecificationTypeInfoResponseDto;
import by.bsu.n1jel.pc.assembler.entity.SpecificationType;
import by.bsu.n1jel.pc.assembler.entity.enums.SpecValueType;
import by.bsu.n1jel.pc.assembler.exception.InvalidRequestException;
import by.bsu.n1jel.pc.assembler.mapper.SpecificationMapper;
import by.bsu.n1jel.pc.assembler.repository.SpecificationTypeRepository;
import by.bsu.n1jel.pc.assembler.service.api.SpecificationService;
import by.bsu.n1jel.pc.assembler.service.utils.OverallUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static by.bsu.n1jel.pc.assembler.exception.common.ResourceExceptionFactory.specificationTypeNotFoundException;

@Service
@RequiredArgsConstructor
public class SpecificationServiceImpl implements SpecificationService {

    private final SpecificationTypeRepository specsTypeRepository;
    private final SpecificationMapper specsMapper;
    private final SearchDao searchDao;

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private SpecificationType findSpecificationTypeById(Long specificationId) {
        return specsTypeRepository.findById(specificationId)
                .orElseThrow(
                        () -> specificationTypeNotFoundException(specificationId)
                );
    }


    @Override
    @Transactional(readOnly = true)
    public SpecificationTypeInfoResponseDto getSpecificationTypeById(Long specificationId) {
        return specsMapper.mapTypeToResponse(findSpecificationTypeById(specificationId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecificationTypeInfoResponseDto> getAllSpecificationTypes() {
        return specsMapper.mapTypeToResponse(specsTypeRepository.findAll());
    }

    @Override
    @Transactional
    public SpecificationTypeInfoResponseDto createSpecificationType(SpecificationTypeCreateRequestDto requestDto) {
        SpecificationType specification = SpecificationType.builder()
                .name(requestDto.getName())
                .valueType(requestDto.getValueType() != null ? requestDto.getValueType() : SpecValueType.STRING)
                .unit(blankToNull(requestDto.getUnit()))
                .build();

        if (requestDto.getDescription() != null && !requestDto.getDescription().isEmpty()) {
            specification.setDescription(requestDto.getDescription());
        }

        return specsMapper.mapTypeToResponse(specsTypeRepository.save(specification));
    }

    @Override
    @Transactional
    public SpecificationTypeInfoResponseDto editSpecificationType(SpecificationTypeEditRequestDto requestDto) {
        SpecificationType specificationFromDb = findSpecificationTypeById(requestDto.id());

        // от типа значения системной характеристики зависят проверки совместимости
        if (specificationFromDb.getCode() != null && requestDto.valueType() != null
                && requestDto.valueType() != specificationFromDb.getValueType()) {
            throw new InvalidRequestException("Тип значения системной характеристики «%s» менять нельзя"
                    .formatted(specificationFromDb.getName()));
        }

        specificationFromDb = specsMapper.updateSpecificationType(specificationFromDb, requestDto);
        return specsMapper.mapTypeToResponse(specsTypeRepository.save(specificationFromDb));
    }

    @Override
    @Transactional
    public SpecificationTypeInfoResponseDto deleteSpecificationTypeById(Long specificationId) {
        SpecificationType specificationFromDb = findSpecificationTypeById(specificationId);
        if (specificationFromDb.getCode() != null) {
            throw new InvalidRequestException("Характеристику «%s» использует проверка совместимости, её нельзя удалить"
                    .formatted(specificationFromDb.getName()));
        }
        SpecificationTypeInfoResponseDto responseDto = specsMapper.mapTypeToResponse(specificationFromDb);
        specsTypeRepository.delete(specificationFromDb);
        return responseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SpecificationTypeInfoResponseDto> searchSpecificationTypes(SpecificationTypeFilterRequestDto requestDto, Integer pageNumber) {
        Page<SpecificationType> specificationTypes = searchDao.searchSpecificationTypes(requestDto, PageRequest.of(--pageNumber, OverallUtil.getPageSize()));
        return specificationTypes.map(specsMapper::mapTypeToResponse);
    }
}
