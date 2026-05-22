package by.bsu.n1jel.pc.assembler.controller.rest;


import by.bsu.n1jel.pc.assembler.dto.request.create.ComponentCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.create.ComponentTypeCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.ComponentEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.ComponentTypeEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.ComponentInfoResponseDto;
import by.bsu.n1jel.pc.assembler.dto.response.ComponentTypeInfoResponseDto;
import by.bsu.n1jel.pc.assembler.service.api.ComponentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Component", description = "Component rest controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/components")
public class RestComponentController {

    private final ComponentService componentService;

    // COMPONENT ENDPOINTS

    @Operation(
            summary = "Get all components",
            description = "Return all existed components from database"
    )
    @GetMapping()
    public List<ComponentInfoResponseDto> getAllComponents() {
        return componentService.getAllComponents();
    }

    @Operation(
            summary = "Get component by id",
            description = "Return component by it's id"
    )
    @GetMapping("/{componentId}")
    ComponentInfoResponseDto getComponentById(@PathVariable Long componentId) {
        return componentService.getComponentById(componentId);
    }

    @Operation(
            summary = "Create component",
            description = "Creating new component from request"
    )
    @PostMapping("/create")
    ComponentInfoResponseDto createComponent(@RequestBody ComponentCreateRequestDto requestDto) {
        return componentService.createComponent(requestDto);
    }

    @Operation(
            summary = "Get components by type",
            description = "Returning all components with requested type id from database"
    )
    @GetMapping("/type/{componentTypeId}")
    List<ComponentInfoResponseDto> getComponentsByComponentType(@PathVariable Long componentTypeId) {
        return componentService.getComponentsByComponentType(componentTypeId);
    }

    @Operation(
            summary = "Edit component",
            description = "Editing existing parameters of component"
    )
    @PatchMapping("/edit")
    ComponentInfoResponseDto editComponentInfo(@RequestBody ComponentEditRequestDto requestDto) {
        return componentService.editComponentInfo(requestDto);
    }

    @Operation(
            summary = "Delete component",
            description = "Deleting existing component by id"
    )
    @DeleteMapping("/{componentId}")
    ComponentInfoResponseDto deleteComponentById(@PathVariable Long componentId) {
        return componentService.deleteComponentById(componentId);
    }

    // COMPONENT_TYPE ENDPOINTS

    @Operation(
            summary = "Get all types",
            description = "Return all existed component types from database"
    )
    @GetMapping("/types")
    List<ComponentTypeInfoResponseDto> getAllComponentTypes() {
        return componentService.getAllComponentTypes();
    }

    @Operation(
            summary = "Get type by id",
            description = "Return component type by it's id"
    )
    @GetMapping("/types/{componentTypeId}")
    ComponentTypeInfoResponseDto getComponentTypeById(@PathVariable Long componentTypeId) {
        return componentService.getComponentTypeById(componentTypeId);
    }

    @Operation(
            summary = "Create type",
            description = "Creating new component type from request"
    )
    @PostMapping("/types/create")
    ComponentTypeInfoResponseDto createComponentType(@RequestBody ComponentTypeCreateRequestDto requestDto) {
        return componentService.createComponentType(requestDto);
    }

    @Operation(
            summary = "Edit type",
            description = "Editing existing parameters of component type"
    )
    @PatchMapping("/types/edit")
    ComponentTypeInfoResponseDto editComponentType(@RequestBody ComponentTypeEditRequestDto requestDto) {
        return componentService.editComponentType(requestDto);
    }

    @Operation(
            summary = "Delete type",
            description = "Deleting existing component type by id"
    )
    @DeleteMapping("/types/{componentTypeId}")
    ComponentTypeInfoResponseDto deleteComponentTypeById(@PathVariable Long componentTypeId) {
        return componentService.deleteComponentTypeById(componentTypeId);
    }

}
