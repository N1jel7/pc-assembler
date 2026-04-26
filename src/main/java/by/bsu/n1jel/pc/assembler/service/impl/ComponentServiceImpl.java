package by.bsu.n1jel.pc.assembler.service.impl;

import by.bsu.n1jel.pc.assembler.dto.request.*;
import by.bsu.n1jel.pc.assembler.dto.response.ComponentInfoResponseDto;
import by.bsu.n1jel.pc.assembler.entity.*;
import by.bsu.n1jel.pc.assembler.mapper.ComponentMapper;
import by.bsu.n1jel.pc.assembler.repository.*;
import by.bsu.n1jel.pc.assembler.service.api.ComponentService;
import lombok.RequiredArgsConstructor;
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
                .name(requestDto.name())
                .producer(findProducerById(requestDto.producer()))
                .componentType(findComponentTypeById(requestDto.componentType()))
                .price(requestDto.price())
                .stockQuantity(requestDto.stockQuantity())
                .build();

        createdComponent = componentRepository.save(createdComponent);


        List<Specification> createdSpecifications = new ArrayList<>();

        for (SpecificationCreateRequestDto spec : requestDto.specifications()) {
            createdSpecifications.add(Specification.builder()
                    .type(findSpecificationTypeById(spec.specificationTypeId()))
                    .component(createdComponent)
                    .value(spec.value())
                    .build());

        }

        createdComponent.setSpecifications(createdSpecifications);

        return componentMapper.mapToResponseDto(componentRepository.save(createdComponent));
    }


    @Override
    @Transactional(readOnly = true)
    public List<ComponentInfoResponseDto> getAllComponents() {
        return componentMapper.mapToResponseDto(componentRepository.findAll());
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

        if(!findableComponents.isEmpty()) {
            return componentMapper.mapToResponseDto(findableComponents);
        }

        return null;
    }

    @Override
    @Transactional
    public ComponentInfoResponseDto editComponentInfo(ComponentEditRequestDto requestDto) {
        Component componentFromDb = findComponentById(requestDto.id());

        componentFromDb = componentMapper.updateComponent(componentFromDb, requestDto);

        return componentMapper.mapToResponseDto(componentRepository.save(updateDifficultComponentData(componentFromDb, requestDto)));
    }

    private Component updateDifficultComponentData(Component componentFromDb, ComponentEditRequestDto requestDto) {
        if (requestDto.componentType() != null) {
            componentFromDb.setComponentType(findComponentTypeById(requestDto.componentType()));
        }

        if (requestDto.producer() != null) {
            componentFromDb.setProducer(findProducerById(requestDto.producer()));
        }

        if (requestDto.specifications() != null) {
            List<Specification> specifications = componentFromDb.getSpecifications();
            requestDto.specifications().forEach(dtoSpec -> {
                specifications.iterator().forEachRemaining(objectSpec -> {
                    if (dtoSpec.id().equals(objectSpec.getId())) {
                        objectSpec.setValue(dtoSpec.value());
                    }
                });
            });

            componentFromDb.setSpecifications(specifications);

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
    public List<ComponentTypeInfoResponseDto> getAllComponentTypes() {
        return componentMapper.mapToTypeResponseDto(componentTypeRepository.findAll());
    }

    @Override
    public ComponentTypeInfoResponseDto getComponentTypeById(Long componentTypeId) {
        return componentMapper.mapToTypeResponseDto(findComponentTypeById(componentTypeId));
    }

    @Override
    @Transactional
    public ComponentTypeInfoResponseDto createComponentType(ComponentTypeCreateRequestDto requestDto) {

        ComponentType createdComponentType = ComponentType.builder()
                .name(requestDto.name())
                .build();

        if (requestDto.componentParentType() != null) {
            createdComponentType.setComponentParentType(findComponentTypeById(requestDto.componentParentType()));
        }
        return componentMapper.mapToTypeResponseDto(componentTypeRepository.save(createdComponentType));
    }

    @Override
    @Transactional
    public ComponentTypeInfoResponseDto editComponentType(ComponentTypeEditRequestDto requestDto) {
        ComponentType componentTypeFromDb = findComponentTypeById(requestDto.id());
        componentTypeFromDb = componentMapper.updateComponentType(componentTypeFromDb, requestDto);

        if (requestDto.componentParentType() != null) {
            componentTypeFromDb.setComponentParentType(findComponentTypeById(requestDto.componentParentType()));
        }

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
}
