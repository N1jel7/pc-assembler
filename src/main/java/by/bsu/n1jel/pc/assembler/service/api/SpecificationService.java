package by.bsu.n1jel.pc.assembler.service.api;

import by.bsu.n1jel.pc.assembler.dto.request.create.SpecificationTypeCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.SpecificationTypeEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.SpecificationTypeInfoResponseDto;

import java.util.List;

public interface SpecificationService {

    SpecificationTypeInfoResponseDto getSpecificationTypeById(Long specificationId);

    List<SpecificationTypeInfoResponseDto> getAllSpecificationTypes();

    SpecificationTypeInfoResponseDto createSpecificationType(SpecificationTypeCreateRequestDto requestDto);

    SpecificationTypeInfoResponseDto editSpecificationType(SpecificationTypeEditRequestDto requestDto);

    SpecificationTypeInfoResponseDto deleteSpecificationTypeById(Long specificationId);
}
