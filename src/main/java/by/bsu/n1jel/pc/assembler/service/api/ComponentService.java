package by.bsu.n1jel.pc.assembler.service.api;

import by.bsu.n1jel.pc.assembler.dto.request.*;
import by.bsu.n1jel.pc.assembler.dto.response.ComponentInfoResponseDto;

import java.util.List;

public interface ComponentService {

    List<ComponentInfoResponseDto> getAllComponents();

    ComponentInfoResponseDto getComponentById(Long componentId);

    ComponentInfoResponseDto createComponent(ComponentCreateRequestDto requestDto);

    List<ComponentInfoResponseDto> getComponentsByComponentType(Long componentTypeId);

    ComponentInfoResponseDto editComponentInfo(ComponentEditRequestDto requestDto);

    ComponentInfoResponseDto deleteComponentById(Long componentId);

    List<ComponentTypeInfoResponseDto> getAllComponentTypes();

    ComponentTypeInfoResponseDto getComponentTypeById(Long componentTypeId);

    ComponentTypeInfoResponseDto createComponentType(ComponentTypeCreateRequestDto requestDto);

    ComponentTypeInfoResponseDto editComponentType(ComponentTypeEditRequestDto requestDto);

    ComponentTypeInfoResponseDto deleteComponentTypeById(Long componentTypeId);

}
