package by.bsu.n1jel.pc.assembler.service.impl;

import by.bsu.n1jel.pc.assembler.dao.SearchDao;
import by.bsu.n1jel.pc.assembler.dto.request.create.ComponentCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.create.ComponentTypeCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.create.SpecificationCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.ComponentEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.ComponentTypeEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.SpecificationEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.search.ComponentFilterRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.ComponentInfoResponseDto;
import by.bsu.n1jel.pc.assembler.dto.response.ComponentTypeInfoResponseDto;
import by.bsu.n1jel.pc.assembler.entity.*;
import by.bsu.n1jel.pc.assembler.mapper.ComponentMapper;
import by.bsu.n1jel.pc.assembler.repository.*;
import by.bsu.n1jel.pc.assembler.service.api.ComponentService;
import by.bsu.n1jel.pc.assembler.service.utils.OverallUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static by.bsu.n1jel.pc.assembler.exception.common.ResourceExceptionFactory.*;

@Service
@RequiredArgsConstructor
public class ComponentServiceImpl implements ComponentService {

    private final ComponentTypeRepository componentTypeRepository;
    private final ProducerRepository producerRepository;
    private final ComponentRepository componentRepository;
    private final SpecificationTypeRepository specsTypeRepository;
    private final SpecificationRepository specsRepository;
    private final ComponentMapper componentMapper;
    private final SearchDao searchDao;

    private Component findComponentById(Long componentId) {
        return componentRepository.findById(componentId)
                .orElseThrow(
                        () -> componentNotFoundException(componentId)
                );
    }

    private Producer findProducerById(Long producerId) {
        return producerRepository.findById(producerId)
                .orElseThrow(
                        () -> producerNotFoundException(producerId)
                );
    }

    private ComponentType findComponentTypeById(Long componentTypeId) {
        return componentTypeRepository.findById(componentTypeId)
                .orElseThrow(
                        () -> componentTypeNotFoundException(componentTypeId)
                );
    }

    private Specification findSpecificationById(Long specificationId) {
        return specsRepository.findById(specificationId)
                .orElseThrow(
                        () -> specificationNotFoundException(specificationId)
                );
    }

    private SpecificationType findSpecificationTypeById(Long specificationTypeId) {
        return specsTypeRepository.findById(specificationTypeId)
                .orElseThrow(
                        () -> specificationTypeNotFoundException(specificationTypeId)
                );
    }

    @Override
    @Transactional
    public ComponentInfoResponseDto createComponent(ComponentCreateRequestDto requestDto) {
        Component createdComponent = Component.builder()
                .name(requestDto.getName())
                .producer(findProducerById(requestDto.getProducer()))
                .componentType(findComponentTypeById(requestDto.getComponentType()))
                .price(requestDto.getPrice())
                .stockQuantity(requestDto.getStockQuantity())
                .build();

        createdComponent = componentRepository.save(createdComponent);


        List<Specification> createdSpecifications = new ArrayList<>();

        for (SpecificationCreateRequestDto spec : requestDto.getSpecifications()) {
            createdSpecifications.add(Specification.builder()
                    .type(findSpecificationTypeById(spec.getSpecificationTypeId()))
                    .component(createdComponent)
                    .value(spec.getValue())
                    .build());

        }

        createdComponent.setSpecifications(createdSpecifications);

        return componentMapper.mapToResponseDto(componentRepository.save(createdComponent));
    }


    @Override
    @Transactional(readOnly = true)
    public Page<ComponentInfoResponseDto> findComponentsBySearchFilter(ComponentFilterRequestDto requestDto, Integer pageNumber) {
        Page<Component> componentPage = searchDao.searchComponents(requestDto, PageRequest.of(--pageNumber, OverallUtil.getPageSize()));
        return componentPage.map(componentMapper::mapToResponseDto);
    }

    @Override
    public String getComponentTypeNameById(Long componentTypeId) {
        return componentTypeRepository.getComponentTypeNameById(componentTypeId);
    }


    @Override
    @Transactional(readOnly = true)
    public List<ComponentInfoResponseDto> getAllComponents() {
        return componentMapper.mapToResponseDto(componentRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ComponentInfoResponseDto> getComponentsByPage(Integer pageNumber) {
        Page<Component> componentPage = componentRepository.findAll(PageRequest.of(--pageNumber, OverallUtil.getPageSize()));
        return componentPage.map(componentMapper::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ComponentInfoResponseDto getComponentById(Long componentId) {
        return componentMapper.mapToResponseDto(findComponentById(componentId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComponentInfoResponseDto> getComponentsByComponentType(Long componentTypeId) {
        ComponentType findableType = findComponentTypeById(componentTypeId);
        List<Component> findableComponents = componentRepository.findComponentsByComponentType(findableType);

        if (!findableComponents.isEmpty()) {
            return componentMapper.mapToResponseDto(findableComponents);
        }

        return null;
    }

    @Override
    @Transactional
    public ComponentInfoResponseDto editComponentInfo(ComponentEditRequestDto requestDto) {
        Component componentFromDb = findComponentById(requestDto.getId());

        componentFromDb = componentMapper.updateComponent(componentFromDb, requestDto);

        return componentMapper.mapToResponseDto(componentRepository.save(updateDifficultComponentData(componentFromDb, requestDto)));
    }

    private Component updateDifficultComponentData(Component componentFromDb, ComponentEditRequestDto requestDto) {
        if (requestDto.getComponentType() != null) {
            componentFromDb.setComponentType(findComponentTypeById(requestDto.getComponentType()));
        }

        if (requestDto.getProducer() != null) {
            componentFromDb.setProducer(findProducerById(requestDto.getProducer()));
        }

        if (requestDto.getSpecifications() != null) {
            List<Specification> existingSpecs = componentFromDb.getSpecifications();

            List<Specification> updatedSpecs = new ArrayList<>();

            for (SpecificationEditRequestDto dtoSpec : requestDto.getSpecifications()) {
                boolean found = false;

                for (Specification existingSpec : existingSpecs) {
                    if (dtoSpec.getId().equals(existingSpec.getId())) {
                        existingSpec.setValue(dtoSpec.getValue());
                        updatedSpecs.add(existingSpec);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    Specification newSpec = Specification.builder()
                            .component(componentFromDb)
                            .type(findSpecificationTypeById(dtoSpec.getId()))
                            .value(dtoSpec.getValue())
                            .build();
                    updatedSpecs.add(newSpec);
                }
            }

            componentFromDb.setSpecifications(updatedSpecs);
        }

        return componentFromDb;
    }


@Override
@Transactional
public ComponentInfoResponseDto deleteComponentById(Long componentId) {
    Component componentFromDb = findComponentById(componentId);
    ComponentInfoResponseDto responseDto = componentMapper.mapToResponseDto(componentFromDb);
    componentRepository.delete(componentFromDb);
    return responseDto;
}

@Override
@Transactional(readOnly = true)
public List<ComponentTypeInfoResponseDto> getAllComponentTypes() {
    return componentMapper.mapToTypeResponseDto(componentTypeRepository.findAll());
}

@Override
@Transactional(readOnly = true)
public ComponentTypeInfoResponseDto getComponentTypeById(Long componentTypeId) {
    return componentMapper.mapToTypeResponseDto(findComponentTypeById(componentTypeId));
}

@Override
@Transactional
public ComponentTypeInfoResponseDto createComponentType(ComponentTypeCreateRequestDto requestDto) {

    ComponentType createdComponentType = ComponentType.builder()
            .name(requestDto.name())
            .build();

    return componentMapper.mapToTypeResponseDto(componentTypeRepository.save(createdComponentType));
}

@Override
@Transactional
public ComponentTypeInfoResponseDto editComponentType(ComponentTypeEditRequestDto requestDto) {
    ComponentType componentTypeFromDb = findComponentTypeById(requestDto.id());
    componentTypeFromDb = componentMapper.updateComponentType(componentTypeFromDb, requestDto);

    return componentMapper.mapToTypeResponseDto(componentTypeRepository.save(componentTypeFromDb));
}

@Override
@Transactional
public ComponentTypeInfoResponseDto deleteComponentTypeById(Long componentTypeId) {
    ComponentType componentTypeFromDb = findComponentTypeById(componentTypeId);
    ComponentTypeInfoResponseDto responseDto = componentMapper.mapToTypeResponseDto(componentTypeFromDb);
    componentTypeRepository.delete(componentTypeFromDb);
    return responseDto;
}

@Override
public List<String> getAllProcessorProducers() {
    return componentRepository.getAllProcessorProducers();
}

@Override
public Integer getAllComponentTypesSize() {
    return componentTypeRepository.getAllComponentTypesSize();
}

@Override
@Transactional(readOnly = true)
public List<ComponentInfoResponseDto> getLatestComponents() {
    return componentMapper.mapToResponseDto(componentRepository.findLatestComponents(OverallUtil.getLatestObjectSize()));
}
}
