package by.bsu.n1jel.pc.assembler.service.api;

import by.bsu.n1jel.pc.assembler.dto.request.create.ComponentCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.create.ComponentTypeCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.ComponentEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.ComponentTypeEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.ComponentInfoResponseDto;
import by.bsu.n1jel.pc.assembler.dto.response.ComponentTypeInfoResponseDto;
import by.bsu.n1jel.pc.assembler.dto.request.search.ComponentFilterRequestDto;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ComponentService {

    // SEARCH

    Page<ComponentInfoResponseDto> findComponentsBySearchFilter(ComponentFilterRequestDto requestDto, Integer pageNumber);

    // COMPONENTS

    String getComponentTypeNameById(Long componentTypeId);

    List<ComponentInfoResponseDto> getLatestComponents();

    List<ComponentInfoResponseDto> getAllComponents();

    Page<ComponentInfoResponseDto> getComponentsByPage(Integer pageNumber);

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

    // OTHER

    List<String> getAllProcessorProducers();

    Integer getAllComponentTypesSize();
}
