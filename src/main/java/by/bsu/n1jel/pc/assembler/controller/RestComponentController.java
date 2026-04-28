package by.bsu.n1jel.pc.assembler.controller;


import by.bsu.n1jel.pc.assembler.dto.request.create.ComponentCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.create.ComponentTypeCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.ComponentEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.ComponentTypeEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.ComponentInfoResponseDto;
import by.bsu.n1jel.pc.assembler.dto.response.ComponentTypeInfoResponseDto;
import by.bsu.n1jel.pc.assembler.service.api.ComponentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/components")
public class RestComponentController {

    private final ComponentService componentService;

    // COMPONENT ENDPOINTS

    @GetMapping()
    public List<ComponentInfoResponseDto> getAllComponents() {
        return componentService.getAllComponents();
    }

    @GetMapping("/{componentId}")
    ComponentInfoResponseDto getComponentById(@PathVariable Long componentId) {
        return componentService.getComponentById(componentId);
    }

    @PostMapping("/create")
    ComponentInfoResponseDto createComponent(@RequestBody ComponentCreateRequestDto requestDto) {
        return componentService.createComponent(requestDto);
    }

    @GetMapping("/type/{componentTypeId}")
    List<ComponentInfoResponseDto> getComponentsByComponentType(@PathVariable Long componentTypeId) {
        return componentService.getComponentsByComponentType(componentTypeId);
    }

    @PatchMapping("/edit")
    ComponentInfoResponseDto editComponentInfo(@RequestBody ComponentEditRequestDto requestDto) {
        return componentService.editComponentInfo(requestDto);
    }

    @DeleteMapping("/{componentId}")
    ComponentInfoResponseDto deleteComponentById(@PathVariable Long componentId) {
        return componentService.deleteComponentById(componentId);
    }

    // COMPONENT_TYPE ENDPOINTS

    @GetMapping("/types")
    List<ComponentTypeInfoResponseDto> getAllComponentTypes() {
        return componentService.getAllComponentTypes();
    }

    @GetMapping("/types/{componentTypeId}")
    ComponentTypeInfoResponseDto getComponentTypeById(@PathVariable Long componentTypeId) {
        return componentService.getComponentTypeById(componentTypeId);
    }

    @PostMapping("/types/create")
    ComponentTypeInfoResponseDto createComponentType(@RequestBody ComponentTypeCreateRequestDto requestDto) {
        return componentService.createComponentType(requestDto);
    }

    @PatchMapping("/types/edit")
    ComponentTypeInfoResponseDto editComponentType(@RequestBody ComponentTypeEditRequestDto requestDto) {
        return componentService.editComponentType(requestDto);
    }

    @DeleteMapping("/types/{componentTypeId}")
    ComponentTypeInfoResponseDto deleteComponentTypeById(@PathVariable Long componentTypeId) {
        return componentService.deleteComponentTypeById(componentTypeId);
    }

}
