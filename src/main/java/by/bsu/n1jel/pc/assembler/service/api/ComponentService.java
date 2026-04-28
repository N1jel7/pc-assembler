package by.bsu.n1jel.pc.assembler.service.api;

import by.bsu.n1jel.pc.assembler.dto.request.create.ComponentCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.create.ComponentTypeCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.ComponentEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.ComponentTypeEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.ComponentInfoResponseDto;
import by.bsu.n1jel.pc.assembler.dto.response.ComponentTypeInfoResponseDto;

import java.util.List;

public interface ComponentService {

    // COMPONENTS

    List<ComponentInfoResponseDto> getAllComponents();

    ComponentInfoResponseDto getComponentById(Long componentId);

    ComponentInfoResponseDto createComponent(ComponentCreateRequestDto requestDto);

    List<ComponentInfoResponseDto> getComponentsByComponentType(Long componentTypeId);

    ComponentInfoResponseDto editComponentInfo(ComponentEditRequestDto requestDto);

    ComponentInfoResponseDto deleteComponentById(Long componentId);

    // COMPONENT TYPES

    List<ComponentTypeInfoResponseDto> getAllComponentTypes();

    ComponentTypeInfoResponseDto getComponentTypeById(Long componentTypeId);

    ComponentTypeInfoResponseDto createComponentType(ComponentTypeCreateRequestDto requestDto);

    ComponentTypeInfoResponseDto editComponentType(ComponentTypeEditRequestDto requestDto);

    ComponentTypeInfoResponseDto deleteComponentTypeById(Long componentTypeId);

}
