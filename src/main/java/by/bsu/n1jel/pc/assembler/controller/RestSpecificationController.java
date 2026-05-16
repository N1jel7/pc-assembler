package by.bsu.n1jel.pc.assembler.controller;

import by.bsu.n1jel.pc.assembler.dto.request.create.SpecificationTypeCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.SpecificationTypeEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.SpecificationTypeInfoResponseDto;
import by.bsu.n1jel.pc.assembler.service.api.SpecificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Specification", description = "Specification rest controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/specifications/types")
public class RestSpecificationController {

    private final SpecificationService specificationService;

    @Operation(
            summary = "Get all specifications",
            description = "Return all existed specifications from database"
    )
    @GetMapping
    public List<SpecificationTypeInfoResponseDto> getAllSpecifications() {
        return specificationService.getAllSpecificationTypes();
    }

    @Operation(
            summary = "Get specification by id",
            description = "Return specification by it's id"
    )
    @GetMapping("/{specificationId}")
    public SpecificationTypeInfoResponseDto getSpecificationById(@PathVariable Long specificationId) {
        return specificationService.getSpecificationTypeById(specificationId);
    }

    @Operation(
            summary = "Create specification",
            description = "Creating new specification from request"
    )
    @PostMapping("/create")
    public SpecificationTypeInfoResponseDto createSpecification(@RequestBody SpecificationTypeCreateRequestDto requestDto) {
        return specificationService.createSpecificationType(requestDto);
    }

    @Operation(
            summary = "Edit specification",
            description = "Editing existing parameters of specification"
    )
    @PatchMapping("/edit")
    public SpecificationTypeInfoResponseDto editSpecification(@RequestBody SpecificationTypeEditRequestDto requestDto) {
        return specificationService.editSpecificationType(requestDto);
    }

    @Operation(
            summary = "Delete specification",
            description = "Deleting existing specification by id"
    )
    @DeleteMapping("/delete/{specificationId}")
    public SpecificationTypeInfoResponseDto deleteSpecification(@PathVariable Long specificationId) {
        return specificationService.deleteSpecificationTypeById(specificationId);
    }
}
