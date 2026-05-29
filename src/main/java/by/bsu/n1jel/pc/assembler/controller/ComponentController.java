package by.bsu.n1jel.pc.assembler.controller;

import by.bsu.n1jel.pc.assembler.dto.request.create.ComponentCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.ComponentEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.search.ComponentFilterRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.ComponentInfoResponseDto;
import by.bsu.n1jel.pc.assembler.dto.response.ProducerInfoResponseDto;
import by.bsu.n1jel.pc.assembler.service.api.ComponentService;
import by.bsu.n1jel.pc.assembler.service.api.ProducerService;
import by.bsu.n1jel.pc.assembler.service.api.SpecificationService;
import by.bsu.n1jel.pc.assembler.service.utils.DefaultImageUtil;
import by.bsu.n1jel.pc.assembler.service.utils.OverallUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/components")
@RequiredArgsConstructor
public class ComponentController {

    private final ComponentService componentService;
    private final ProducerService producerService;
    private final SpecificationService specificationService;

    @GetMapping
    public String list(@ModelAttribute ComponentFilterRequestDto componentFilter,
                       @RequestParam(defaultValue = "1") int page,
                       Model model) {

        Page<ComponentInfoResponseDto> infoDto = componentService.findComponentsBySearchFilter(componentFilter, page);

        model.addAttribute("activePage", "components");
        model.addAttribute("pageTitle", "Компоненты");
        model.addAttribute("componentFilter", componentFilter);
        model.addAttribute("components", infoDto);
        model.addAttribute("producers", producerService.getAllProducers());
        model.addAttribute("componentTypes", componentService.getAllComponentTypes());
        model.addAttribute("currentPage", infoDto.getTotalPages() == 0 ? 1 : infoDto.getNumber() + 1);
        model.addAttribute("totalPages", infoDto.getTotalPages());
        model.addAttribute("totalElements", infoDto.getTotalElements());

        model.addAttribute("selectionMode", false);
        model.addAttribute("selectedSlotKey", null);
        model.addAttribute("selectedSlotLabel", null);
        model.addAttribute("selectedSlotType", null);
        model.addAttribute("buildId", null);
        model.addAttribute("returnUrl", null);

        return "components/list";
    }

    @GetMapping("/select")
    public String select(@ModelAttribute ComponentFilterRequestDto componentFilter,
                         @RequestParam Long buildId,
                         @RequestParam String returnUrl,
                         @RequestParam(defaultValue = "1") int page,
                         Model model) {

        Page<ComponentInfoResponseDto> infoDto = componentService.findComponentsBySearchFilter(componentFilter, page);

        model.addAttribute("activePage", "components");
        model.addAttribute("pageTitle", "Выбор компонента");
        model.addAttribute("componentFilter", componentFilter);
        model.addAttribute("components", infoDto);
        model.addAttribute("producers", producerService.getAllProducers());
        model.addAttribute("componentTypes", componentService.getAllComponentTypes());
        model.addAttribute("currentPage", infoDto.getTotalPages() == 0 ? 1 : infoDto.getNumber() + 1);
        model.addAttribute("totalPages", infoDto.getTotalPages());
        model.addAttribute("totalElements", infoDto.getTotalElements());

        model.addAttribute("selectionMode", true);
        model.addAttribute("selectedSlotKey", componentFilter.componentType());
        model.addAttribute("selectedSlotLabel", componentService.getComponentTypeNameById(componentFilter.componentType()));
        model.addAttribute("selectedSlotType", componentFilter.componentType());
        model.addAttribute("buildId", buildId);
        model.addAttribute("returnUrl", returnUrl);

        return "components/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {

        ComponentInfoResponseDto componentDto = componentService.getComponentById(id);
        ProducerInfoResponseDto producerDto = producerService.getProducerById(componentDto.getProducerId());

        model.addAttribute("activePage", "components");
        model.addAttribute("pageTitle", componentDto.getName());
        model.addAttribute("component", componentDto);
        model.addAttribute("avatar",
                componentDto.getAvatar() != null && !componentDto.getAvatar().isBlank()
                        ? componentDto.getAvatar()
                        : DefaultImageUtil.buildAvatar(componentDto.getName()));
        model.addAttribute("producer", producerDto);
        model.addAttribute("specifications", componentDto.getSpecifications());
        model.addAttribute("componentPhotos", List.of());

        return "components/detail";
    }

    @GetMapping("/create")
    public String createForm(Model model) {

        model.addAttribute("activePage", "components");
        model.addAttribute("pageTitle", "Создать компонент");
        model.addAttribute("mode", "create");
        model.addAttribute("defaultAvatar",
                DefaultImageUtil.componentAvatar("Здесь будет тип компонента", "Здесь будет имя компонента"));
        model.addAttribute("component", new ComponentInfoResponseDto());
        model.addAttribute("producers", producerService.getAllProducers());
        model.addAttribute("componentTypes", componentService.getAllComponentTypes());
        model.addAttribute("specificationTypes", specificationService.getAllSpecificationTypes());
        model.addAttribute("existingImages", List.of());
        model.addAttribute("maxImages", OverallUtil.getMaxImagesSize());

        return "components/form";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute ComponentCreateRequestDto requestDto) {
        ComponentInfoResponseDto componentInfoResponseDto = componentService.createComponent(requestDto);
        return "redirect:/components/" + componentInfoResponseDto.getId();
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {

        ComponentInfoResponseDto responseDto = componentService.getComponentById(id);

        model.addAttribute("activePage", "components");
        model.addAttribute("pageTitle", "Редактировать компонент");
        model.addAttribute("mode", "edit");

        model.addAttribute("component", responseDto);

        model.addAttribute("producers", producerService.getAllProducers());
        model.addAttribute("componentTypes", componentService.getAllComponentTypes());
        model.addAttribute("specificationTypes", specificationService.getAllSpecificationTypes());


        model.addAttribute("existingImages", List.of());
        model.addAttribute("maxImages", OverallUtil.getMaxImagesSize());
        model.addAttribute("defaultAvatar",
                DefaultImageUtil.componentAvatar(responseDto.getComponentTypeName(), responseDto.getName()));

        return "components/form";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id, @ModelAttribute ComponentEditRequestDto requestDto) {
        requestDto.setId(id);
        componentService.editComponentInfo(requestDto);
        return "redirect:/components/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        componentService.deleteComponentById(id);
        return "redirect:/components";
    }
}