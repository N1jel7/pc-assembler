package by.bsu.n1jel.pc.assembler.controller;

import by.bsu.n1jel.pc.assembler.dto.request.SpecificationCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.SpecificationEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.SpecificationInfoResponseDto;
import by.bsu.n1jel.pc.assembler.service.api.SpecificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/specifications")
public class RestSpecificationController {

    private final SpecificationService specificationService;

    @GetMapping
    public List<SpecificationInfoResponseDto> getAllSpecification() {
        return specificationService.getAllSpecifications();
    }

    @GetMapping("/{specificationId}")
    public SpecificationInfoResponseDto getSpecificationById(@PathVariable Long specificationId) {
        return specificationService.getSpecificationById(specificationId);
    }

    @PostMapping("/create")
    public SpecificationInfoResponseDto createSpecification(@RequestBody SpecificationCreateRequestDto requestDto) {
        return specificationService.createSpecification(requestDto);
    }

    @PatchMapping("/edit")
    public SpecificationInfoResponseDto editSpecification(@RequestBody SpecificationEditRequestDto requestDto) {
        return specificationService.editSpecification(requestDto);
    }

    @DeleteMapping("/delete/{specificationId}")
    public SpecificationInfoResponseDto deleteSpecification(@PathVariable Long specificationId) {
        return specificationService.deleteSpecificationById(specificationId);
    }
}
