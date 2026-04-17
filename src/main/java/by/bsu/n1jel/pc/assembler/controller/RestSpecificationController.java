package by.bsu.n1jel.pc.assembler.controller;

import by.bsu.n1jel.pc.assembler.dto.request.SpecificationTypeCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.SpecificationTypeEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.SpecificationTypeInfoResponseDto;
import by.bsu.n1jel.pc.assembler.service.api.SpecificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/specifications/types")
public class RestSpecificationController {

    private final SpecificationService specificationService;

    @GetMapping
    public List<SpecificationTypeInfoResponseDto> getAllSpecification() {
        return specificationService.getAllSpecificationTypes();
    }

    @GetMapping("/{specificationId}")
    public SpecificationTypeInfoResponseDto getSpecificationById(@PathVariable Long specificationId) {
        return specificationService.getSpecificationTypeById(specificationId);
    }

    @PostMapping("/create")
    public SpecificationTypeInfoResponseDto createSpecification(@RequestBody SpecificationTypeCreateRequestDto requestDto) {
        return specificationService.createSpecificationType(requestDto);
    }

    @PatchMapping("/edit")
    public SpecificationTypeInfoResponseDto editSpecification(@RequestBody SpecificationTypeEditRequestDto requestDto) {
        return specificationService.editSpecificationType(requestDto);
    }

    @DeleteMapping("/delete/{specificationId}")
    public SpecificationTypeInfoResponseDto deleteSpecification(@PathVariable Long specificationId) {
        return specificationService.deleteSpecificationTypeById(specificationId);
    }
}
