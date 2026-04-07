package by.bsu.n1jel.pc.assembler.service.api;

import by.bsu.n1jel.pc.assembler.dto.request.SpecificationCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.SpecificationEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.SpecificationInfoResponseDto;

import java.util.List;

public interface SpecificationService {

    SpecificationInfoResponseDto getSpecificationById(Long specificationId);

    List<SpecificationInfoResponseDto> getAllSpecifications();

    SpecificationInfoResponseDto createSpecification(SpecificationCreateRequestDto requestDto);

    SpecificationInfoResponseDto editSpecification(SpecificationEditRequestDto requestDto);

    SpecificationInfoResponseDto deleteSpecificationById(Long specificationId);
}
