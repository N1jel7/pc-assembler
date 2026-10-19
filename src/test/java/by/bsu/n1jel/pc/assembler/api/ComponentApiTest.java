package by.bsu.n1jel.pc.assembler.api;

import by.bsu.n1jel.pc.assembler.AbstractIntegrationTest;
import by.bsu.n1jel.pc.assembler.entity.ComponentType;
import by.bsu.n1jel.pc.assembler.repository.ComponentTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ComponentApiTest extends AbstractIntegrationTest {

    private static final String BASE = "/api/v1/components";

    @Autowired
    private ComponentTypeRepository componentTypeRepository;

    private Long producerId;
    private String producerName;
    private Long gpuTypeId;
    private Long specTypeId;
    private String specTypeName;

    @BeforeEach
    void setUp() throws Exception {
        producerName = unique("Producer");
        producerId = createProducer(producerName, "Тайвань");
        gpuTypeId = seededComponentTypeId("Видеокарта");
        specTypeName = unique("VRAM");
        specTypeId = createSpecificationType(specTypeName, "Объём видеопамяти");
    }

    @Test
    void createdComponentContainsRelationsAndSpecifications() throws Exception {
        String name = unique("GPU");
        Long id = createComponent(name, producerId, gpuTypeId, "799.99", 5, specTypeId, "16 GB");

        mockMvc.perform(get(BASE + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.price").value(799.99))
                .andExpect(jsonPath("$.stockQuantity").value(5))
                .andExpect(jsonPath("$.producerId").value(producerId))
                .andExpect(jsonPath("$.producerName").value(producerName))
                .andExpect(jsonPath("$.componentTypeId").value(gpuTypeId))
                .andExpect(jsonPath("$.componentTypeName").value("Видеокарта"))
                .andExpect(jsonPath("$.specifications", hasSize(1)))
                .andExpect(jsonPath("$.specifications[0].specificationTypeId").value(specTypeId))
                .andExpect(jsonPath("$.specifications[0].name").value(specTypeName))
                .andExpect(jsonPath("$.specifications[0].value").value("16 GB"));

        mockMvc.perform(get(BASE + "/type/{typeId}", gpuTypeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id").value(hasItem(id.intValue())));
    }

    @Test
    void componentWithoutSpecificationsCanBeCreated() throws Exception {
        postJson(BASE + "/create", """
                {"name": "%s", "producer": %d, "componentType": %d, "price": 10, "stockQuantity": 1}
                """.formatted(unique("GPU"), producerId, gpuTypeId))
                .andExpect(status().isOk());
    }

    @Test
    void editUpdatesFieldsAndSpecificationValues() throws Exception {
        Long id = createComponent(unique("GPU"), producerId, gpuTypeId, "500.00", 3, specTypeId, "8 GB");
        ResultActions created = mockMvc.perform(get(BASE + "/{id}", id));
        Long specId = readLong(created, "$.specifications[0].id");
        String newName = unique("GPU-renamed");

        patchJson(BASE + "/edit", """
                {
                  "id": %d,
                  "name": "%s",
                  "price": 450.50,
                  "specifications": [{"id": %d, "value": "12 GB"}]
                }
                """.formatted(id, newName, specId))
                .andExpect(status().isOk());

        mockMvc.perform(get(BASE + "/{id}", id))
                .andExpect(jsonPath("$.name").value(newName))
                .andExpect(jsonPath("$.price").value(450.50))
                // не переданные поля не затираются
                .andExpect(jsonPath("$.stockQuantity").value(3))
                .andExpect(jsonPath("$.producerId").value(producerId))
                .andExpect(jsonPath("$.specifications", hasSize(1)))
                .andExpect(jsonPath("$.specifications[0].id").value(specId))
                .andExpect(jsonPath("$.specifications[0].value").value("12 GB"));
    }

    @Test
    void searchFiltersByNamePriceAndStock() throws Exception {
        String prefix = unique("Search");
        Long cheap = createComponent(prefix + " cheap", producerId, gpuTypeId, "100.00", 2, specTypeId, "4 GB");
        Long expensive = createComponent(prefix + " expensive", producerId, gpuTypeId, "900.00", 2, specTypeId, "24 GB");
        Long outOfStock = createComponent(prefix + " none", producerId, gpuTypeId, "300.00", 0, specTypeId, "8 GB");

        postJson(BASE + "/search/1", """
                {"query": "%s"}
                """.formatted(prefix))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(3));

        postJson(BASE + "/search/1", """
                {"query": "%s", "priceFrom": 200, "priceTo": 950}
                """.formatted(prefix))
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.content[*].id").value(not(hasItem(cheap.intValue()))));

        postJson(BASE + "/search/1", """
                {"query": "%s", "inStock": true, "producer": %d, "componentType": %d}
                """.formatted(prefix, producerId, gpuTypeId))
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.content[*].id").value(hasItem(expensive.intValue())))
                .andExpect(jsonPath("$.content[*].id").value(not(hasItem(outOfStock.intValue()))));
    }

    @Test
    void deletedComponentIsNotFound() throws Exception {
        Long id = createComponent(unique("GPU"), producerId, gpuTypeId, "1.00", 1, specTypeId, "1 GB");

        mockMvc.perform(delete(BASE + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
        mockMvc.perform(get(BASE + "/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void createWithUnknownProducerReturns404() throws Exception {
        postJson(BASE + "/create", """
                {"name": "x", "producer": %d, "componentType": %d, "price": 1, "stockQuantity": 1, "specifications": []}
                """.formatted(Long.MAX_VALUE, gpuTypeId))
                .andExpect(status().isNotFound());
    }

    // COMPONENT TYPES

    @Test
    void seededComponentTypesArePresent() throws Exception {
        mockMvc.perform(get(BASE + "/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name").value(hasItem("Процессор")))
                .andExpect(jsonPath("$[*].name").value(hasItem("Оперативная память")));
    }

    @Test
    void componentTypeCrud() throws Exception {
        String name = unique("Type");
        Long id = createComponentType(name);

        mockMvc.perform(get(BASE + "/types/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name));

        String newName = unique("Type-renamed");
        patchJson(BASE + "/types/edit", """
                {"id": %d, "name": "%s"}
                """.formatted(id, newName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(newName));

        mockMvc.perform(delete(BASE + "/types/{id}", id))
                .andExpect(status().isOk());
        mockMvc.perform(get(BASE + "/types/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void componentTypeAmountLimits() throws Exception {
        Long defaultId = createComponentType(unique("Type"));
        assertThat(componentTypeRepository.findById(defaultId)).get()
                .extracting(ComponentType::getMinAmount, ComponentType::getMaxAmount)
                .containsExactly(1, 1);

        Long customId = readLong(postJson(BASE + "/types/create", """
                {"name": "%s", "minAmount": 1, "maxAmount": 4}
                """.formatted(unique("Type"))).andExpect(status().isOk()), "$.id");
        assertThat(componentTypeRepository.findById(customId)).get()
                .extracting(ComponentType::getMinAmount, ComponentType::getMaxAmount)
                .containsExactly(1, 4);
    }

    @Test
    void componentsByTypeWithoutComponentsIsEmptyList() throws Exception {
        Long emptyTypeId = componentTypeRepository.save(ComponentType.builder()
                .name(unique("Empty")).minAmount(1).maxAmount(1).build()).getId();
        mockMvc.perform(get(BASE + "/type/{typeId}", emptyTypeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
