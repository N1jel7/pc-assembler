package by.bsu.n1jel.pc.assembler.controller;

import by.bsu.n1jel.pc.assembler.dto.request.create.SpecificationTypeCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.SpecificationTypeEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.search.SpecificationTypeFilterRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.SpecificationTypeInfoResponseDto;
import by.bsu.n1jel.pc.assembler.service.api.SpecificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/specification-types")
@RequiredArgsConstructor
public class SpecificationTypeController {

    private final SpecificationService specificationService;

    @GetMapping
    public String list(@ModelAttribute SpecificationTypeFilterRequestDto specificationTypeFilter,
                       @RequestParam(defaultValue = "1") int page,
                       Model model) {

        Page<SpecificationTypeInfoResponseDto> result = specificationService.searchSpecificationTypes(specificationTypeFilter, page);

        model.addAttribute("activePage", "specification-types");
        model.addAttribute("pageTitle", "Типы характеристик");
        model.addAttribute("specificationTypeFilter", specificationTypeFilter);
        model.addAttribute("specificationTypes", result.getContent());
        model.addAttribute("currentPage", result.getTotalPages() == 0 ? 1 : result.getNumber() + 1);
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("totalElements", result.getTotalElements());

        return "specification-types/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("activePage", "specification-types");
        model.addAttribute("pageTitle", "Создать тип характеристики");
        model.addAttribute("mode", "create");
        model.addAttribute("specificationTypeForm", new SpecificationTypeCreateRequestDto());

        return "specification-types/form";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute SpecificationTypeCreateRequestDto requestDto) {
        specificationService.createSpecificationType(requestDto);
        return "redirect:/specification-types";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        SpecificationTypeInfoResponseDto infoDto = specificationService.getSpecificationTypeById(id);

        model.addAttribute("activePage", "specification-types");
        model.addAttribute("pageTitle", "Редактировать тип характеристики");
        model.addAttribute("mode", "edit");
        model.addAttribute("specificationTypeForm", infoDto);

        return "specification-types/form";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id, @ModelAttribute SpecificationTypeEditRequestDto requestDto) {

        // TODO Переделать, убрать  создание DTO
        specificationService.editSpecificationType(new SpecificationTypeEditRequestDto(id, requestDto.name(), requestDto.description()));
        return "redirect:/specification-types";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        specificationService.deleteSpecificationTypeById(id);
        return "redirect:/specification-types";
    }
}
