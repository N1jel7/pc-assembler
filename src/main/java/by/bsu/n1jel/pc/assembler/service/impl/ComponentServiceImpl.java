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
import by.bsu.n1jel.pc.assembler.dto.response.ImageInfoResponseDto;
import by.bsu.n1jel.pc.assembler.entity.*;
import by.bsu.n1jel.pc.assembler.entity.enums.ObjectType;
import by.bsu.n1jel.pc.assembler.exception.ImageValidationException;
import by.bsu.n1jel.pc.assembler.mapper.ComponentMapper;
import by.bsu.n1jel.pc.assembler.repository.*;
import by.bsu.n1jel.pc.assembler.service.api.ComponentService;
import by.bsu.n1jel.pc.assembler.service.api.ImageService;
import by.bsu.n1jel.pc.assembler.service.utils.ImageUrlUtil;
import by.bsu.n1jel.pc.assembler.service.utils.OverallUtil;
import org.springframework.web.multipart.MultipartFile;
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
    private final ImageService imageService;

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
        List<SpecificationCreateRequestDto> requestedSpecifications =
                requestDto.getSpecifications() != null ? requestDto.getSpecifications() : List.of();

        for (SpecificationCreateRequestDto spec : requestedSpecifications) {
            if (spec.getSpecificationTypeId() == null) {
                // пустая строка характеристики из формы
                continue;
            }
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
    @Transactional
    public ComponentInfoResponseDto createComponent(ComponentCreateRequestDto requestDto, List<MultipartFile> images) {
        Component component = findComponentById(createComponent(requestDto).getId());
        imageService.syncImages(component.getImages(), List.of(), images, ObjectType.COMPONENT, component.getId());
        return componentMapper.mapToResponseDto(component);
    }

    @Override
    @Transactional
    public ComponentInfoResponseDto editComponentInfo(ComponentEditRequestDto requestDto, List<Long> keptImageIds, List<MultipartFile> images) {
        editComponentInfo(requestDto);
        Component component = findComponentById(requestDto.getId());
        imageService.syncImages(component.getImages(), keptImageIds, images, ObjectType.COMPONENT, component.getId());
        return componentMapper.mapToResponseDto(component);
    }

    @Override
    @Transactional
    public ImageInfoResponseDto addImage(Long componentId, MultipartFile file) {
        Component component = findComponentById(componentId);
        if (component.getImages().size() >= OverallUtil.getMaxImagesSize()) {
            throw new ImageValidationException("У компонента может быть не больше %d фото".formatted(OverallUtil.getMaxImagesSize()));
        }
        Image image = imageService.store(file, ObjectType.COMPONENT, componentId);
        component.getImages().add(image);
        return ImageUrlUtil.toInfo(image);
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
        return componentMapper.mapToResponseDto(findableComponents);
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
            List<Specification> existingSpecs = componentFromDb.getSpecifications() != null
                    ? componentFromDb.getSpecifications()
                    : List.of();

            List<Specification> updatedSpecs = new ArrayList<>();

            for (SpecificationEditRequestDto dtoSpec : requestDto.getSpecifications()) {
                if (dtoSpec.getId() == null && dtoSpec.getSpecificationTypeId() == null) {
                    // пустая строка характеристики из формы
                    continue;
                }

                Specification existingSpec = findExistingSpecification(existingSpecs, updatedSpecs, dtoSpec);

                if (existingSpec != null) {
                    existingSpec.setValue(dtoSpec.getValue());
                    updatedSpecs.add(existingSpec);
                } else {
                    // REST исторически передаёт id типа в поле id, форма — в specificationTypeId
                    Long typeId = dtoSpec.getSpecificationTypeId() != null
                            ? dtoSpec.getSpecificationTypeId()
                            : dtoSpec.getId();
                    Specification newSpec = Specification.builder()
                            .component(componentFromDb)
                            .type(findSpecificationTypeById(typeId))
                            .value(dtoSpec.getValue())
                            .build();
                    updatedSpecs.add(newSpec);
                }
            }

            componentFromDb.setSpecifications(updatedSpecs);
        }

        return componentFromDb;
    }

    /**
     * Существующая характеристика: по id (REST) или по типу (форма — одна характеристика каждого типа).
     */
    private Specification findExistingSpecification(List<Specification> existingSpecs,
                                                    List<Specification> alreadyUpdated,
                                                    SpecificationEditRequestDto dtoSpec) {
        for (Specification spec : existingSpecs) {
            // сравнение по ссылке: equals из @Data рекурсивно ходит по связям сущностей
            if (alreadyUpdated.stream().anyMatch(updated -> updated == spec)) {
                continue;
            }
            if (dtoSpec.getId() != null && dtoSpec.getId().equals(spec.getId())) {
                return spec;
            }
            if (dtoSpec.getSpecificationTypeId() != null && dtoSpec.getSpecificationTypeId().equals(spec.getType().getId())) {
                return spec;
            }
        }
        return null;
    }


@Override
@Transactional
public ComponentInfoResponseDto deleteComponentById(Long componentId) {
    Component componentFromDb = findComponentById(componentId);
    ComponentInfoResponseDto responseDto = componentMapper.mapToResponseDto(componentFromDb);
    imageService.removeFromStorage(componentFromDb.getImages());
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

    // min/max NOT NULL в БД — по умолчанию слот на один компонент
    ComponentType createdComponentType = ComponentType.builder()
            .name(requestDto.name())
            .minAmount(requestDto.minAmount() != null ? requestDto.minAmount() : 1)
            .maxAmount(requestDto.maxAmount() != null ? requestDto.maxAmount() : 1)
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
