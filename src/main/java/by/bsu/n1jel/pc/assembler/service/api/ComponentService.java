package by.bsu.n1jel.pc.assembler.service.api;

import by.bsu.n1jel.pc.assembler.dto.request.*;
import by.bsu.n1jel.pc.assembler.dto.response.ComponentInfoResponseDto;

import java.util.List;

public interface ComponentService {

    ComponentInfoResponseDto createComponent(ComponentCreateRequestDto requestDto);

    ComponentInfoResponseDto findComponentById(Long componentId);

    List<ComponentInfoResponseDto> findComponentsByComponentType(String componentType);

    ComponentInfoResponseDto editComponentInfo(ComponentEditRequestDto requestDto);

    ComponentInfoResponseDto deleteComponentById(Long componentId);

    ComponentTypeInfoResponseDto createComponentType(ComponentTypeCreateRequestDto requestDto);

    ComponentTypeInfoResponseDto editComponentType(ComponentTypeEditRequestDto requestDto);

    ComponentTypeInfoResponseDto deleteComponentTypeById(Long componentTypeId);

}
