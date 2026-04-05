package by.bsu.n1jel.pc.assembler.service.api;

import by.bsu.n1jel.pc.assembler.dto.request.SpecificationCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.SpecificationEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.SpecificationInfoResponseDto;

public interface SpecificationService {

    SpecificationInfoResponseDto createSpecification(SpecificationCreateRequestDto requestDto);

    SpecificationInfoResponseDto editSpecification(SpecificationEditRequestDto requestDto);

    SpecificationInfoResponseDto deleteSpecificationById(Long specificationId);
}
