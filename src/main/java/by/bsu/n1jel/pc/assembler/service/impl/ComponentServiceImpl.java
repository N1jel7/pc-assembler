package by.bsu.n1jel.pc.assembler.service.impl;

import by.bsu.n1jel.pc.assembler.dto.request.*;
import by.bsu.n1jel.pc.assembler.dto.response.ComponentInfoResponseDto;
import by.bsu.n1jel.pc.assembler.service.api.ComponentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComponentServiceImpl implements ComponentService {

    @Override
    public ComponentInfoResponseDto createComponent(ComponentCreateRequestDto requestDto) {
        return null;
    }

    @Override
    public ComponentInfoResponseDto findComponentById(Long componentId) {
        return null;
    }

    @Override
    public List<ComponentInfoResponseDto> findComponentsByComponentType(String componentType) {
        return List.of();
    }

    @Override
    public ComponentInfoResponseDto editComponentInfo(ComponentEditRequestDto requestDto) {
        return null;
    }

    @Override
    public ComponentInfoResponseDto deleteComponentById(Long componentId) {
        return null;
    }

    @Override
    public ComponentTypeInfoResponseDto createComponentType(ComponentTypeCreateRequestDto requestDto) {
        return null;
    }

    @Override
    public ComponentTypeInfoResponseDto editComponentType(ComponentTypeEditRequestDto requestDto) {
        return null;
    }

    @Override
    public ComponentTypeInfoResponseDto deleteComponentTypeById(Long componentTypeId) {
        return null;
    }
}
