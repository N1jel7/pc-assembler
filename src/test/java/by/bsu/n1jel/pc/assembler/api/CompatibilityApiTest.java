package by.bsu.n1jel.pc.assembler.api;

import by.bsu.n1jel.pc.assembler.AbstractIntegrationTest;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Проверка совместимости на тестовых данных из миграций (v4 + v6). */
class CompatibilityApiTest extends AbstractIntegrationTest {

    private Long componentId(String name) throws Exception {
        List<Number> ids = JsonPath.read(body(mockMvc.perform(get("/api/v1/components"))),
                "$[?(@.name == '%s')].id".formatted(name));
        return ids.getFirst().longValue();
    }

    private Long specTypeId(String code) throws Exception {
        List<Number> ids = JsonPath.read(body(mockMvc.perform(get("/api/v1/specifications/types"))),
                "$[?(@.code == '%s')].id".formatted(code));
        return ids.getFirst().longValue();
    }

    private Long buildOf(String... componentNames) throws Exception {
        Long buildId = createBuild(unique("Compat"));
        for (String name : componentNames) {
            createPartition(buildId, componentId(name), 1);
        }
        return buildId;
    }

    @Test
    void seedComponentsHaveTypedSpecifications() throws Exception {
        // раньше характеристики из тестовых данных не были видны: не была заполнена таблица-связка
        mockMvc.perform(get("/api/v1/components/{id}", componentId("Intel Core i9-13900K")))
                .andExpect(jsonPath("$.specifications[?(@.code == 'socket')].value").value(contains("LGA1700")))
                .andExpect(jsonPath("$.specifications[?(@.code == 'tdp_w')].displayValue").value(contains("125 Вт")))
                .andExpect(jsonPath("$.specifications[?(@.code == 'integrated_graphics')].displayValue").value(contains("Да")));

        mockMvc.perform(get("/api/v1/components/types"))
                .andExpect(jsonPath("$[?(@.code == 'CPU')].requiredSpecificationTypeIds[*]", hasSize(6)));

        // кулер переехал в правильный тип
        mockMvc.perform(get("/api/v1/components/{id}", componentId("Noctua NH-D15")))
                .andExpect(jsonPath("$.componentTypeName").value("Кулер процессора"));
    }

    @Test
    void compatibleBuildFromSeedData() throws Exception {
        Long buildId = buildOf(
                "AMD Ryzen 7 7800X3D", "MSI MPG B650 Carbon WiFi", "Corsair Vengeance DDR5 32GB",
                "NVIDIA RTX 4070 Ti", "Samsung 990 Pro 1TB", "Corsair RM850x 850W",
                "Corsair 5000D Airflow", "Deepcool AK620");

        mockMvc.perform(get("/api/v1/builds/{id}/compatibility", buildId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.buildId").value(buildId))
                .andExpect(jsonPath("$.compatible").value(true))
                .andExpect(jsonPath("$.complete").value(true))
                .andExpect(jsonPath("$.errors").value(0))
                .andExpect(jsonPath("$.issues[?(@.severity != 'WARNING')]", empty()));
    }

    @Test
    void incompatibleBuildFromSeedData() throws Exception {
        // AM5-процессор в плату LGA1700 и DDR4 в DDR5-плату, 4090 на 750 Вт
        Long buildId = buildOf(
                "AMD Ryzen 9 7950X", "MSI PRO Z790-A WiFi", "Kingston Fury Beast DDR4 32GB",
                "NVIDIA RTX 4090", "Seasonic Focus GX-750 750W");

        mockMvc.perform(get("/api/v1/builds/{id}/compatibility", buildId))
                .andExpect(jsonPath("$.compatible").value(false))
                .andExpect(jsonPath("$.complete").value(false))
                .andExpect(jsonPath("$.issues[0].severity").value("ERROR"))
                .andExpect(jsonPath("$.issues[?(@.rule == 'socket')].message",
                        contains("Процессор с сокетом AM5 не встанет в материнскую плату с сокетом LGA1700")))
                .andExpect(jsonPath("$.issues[?(@.rule == 'memory' && @.severity == 'ERROR')]", hasSize(2)))
                // 170 + 450 + 100 = 720 из 750 Вт — больше 80%
                .andExpect(jsonPath("$.issues[?(@.rule == 'power')].message", contains(containsString("лучше от 900 Вт"))))
                .andExpect(jsonPath("$.issues[?(@.severity == 'INCOMPLETE')].message", hasItems(
                        "Не выбран обязательный компонент: корпус",
                        "Не выбран накопитель: системе негде храниться")));
    }

    @Test
    void unknownBuildReturns404() throws Exception {
        mockMvc.perform(get("/api/v1/builds/{id}/compatibility", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    void specificationValuesAreValidatedAndNormalized() throws Exception {
        Long producerId = createProducer(unique("Producer"), "США");
        Long boardTypeId = seededComponentTypeId("Материнская плата");

        postJson("/api/v1/components/create", """
                {"name": "%s", "producer": %d, "componentType": %d, "price": 1, "stockQuantity": 1,
                 "specifications": [{"specificationTypeId": %d, "value": "четыре"}]}
                """.formatted(unique("Board"), producerId, boardTypeId, specTypeId("ram_slots")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("«Слоты памяти»: нужно число, получено «четыре»"));

        Long id = readLong(postJson("/api/v1/components/create", """
                {"name": "%s", "producer": %d, "componentType": %d, "price": 1, "stockQuantity": 1,
                 "specifications": [
                   {"specificationTypeId": %d, "value": " 4,0 "},
                   {"specificationTypeId": %d, "value": "ddr4;  DDR5 ,"},
                   {"specificationTypeId": %d, "value": "Есть"}
                 ]}
                """.formatted(unique("Board"), producerId, boardTypeId,
                        specTypeId("ram_slots"), specTypeId("supported_memory_types"), specTypeId("raid_support")))
                .andExpect(status().isOk()), "$.id");

        mockMvc.perform(get("/api/v1/components/{id}", id))
                .andExpect(jsonPath("$.specifications[?(@.code == 'ram_slots')].value").value(contains("4")))
                .andExpect(jsonPath("$.specifications[?(@.code == 'supported_memory_types')].value").value(contains("ddr4, DDR5")))
                .andExpect(jsonPath("$.specifications[?(@.code == 'raid_support')].value").value(contains("true")));
    }

    @Test
    void duplicateSpecificationIsRejected() throws Exception {
        Long socket = specTypeId("socket");
        postJson("/api/v1/components/create", """
                {"name": "%s", "producer": %d, "componentType": %d, "price": 1, "stockQuantity": 1,
                 "specifications": [{"specificationTypeId": %d, "value": "AM5"}, {"specificationTypeId": %d, "value": "AM4"}]}
                """.formatted(unique("CPU"), createProducer(unique("P"), "США"), seededComponentTypeId("Процессор"), socket, socket))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Характеристика «Сокет» указана дважды"));
    }

    @Test
    void systemSpecificationTypeIsProtected() throws Exception {
        Long socket = specTypeId("socket");

        mockMvc.perform(delete("/api/v1/specifications/types/delete/{id}", socket))
                .andExpect(status().isBadRequest());
        patchJson("/api/v1/specifications/types/edit", """
                {"id": %d, "valueType": "NUMBER"}
                """.formatted(socket))
                .andExpect(status().isBadRequest());

        // описание и единицу менять можно
        patchJson("/api/v1/specifications/types/edit", """
                {"id": %d, "description": "Разъём процессора"}
                """.formatted(socket))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("socket"))
                .andExpect(jsonPath("$.valueType").value("STRING"));
    }

    @Test
    void customSpecificationTypeHasValueTypeAndUnit() throws Exception {
        postJson("/api/v1/specifications/types/create", """
                {"name": "%s", "description": "Уровень шума", "valueType": "NUMBER", "unit": "дБ"}
                """.formatted(unique("Noise")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valueType").value("NUMBER"))
                .andExpect(jsonPath("$.unit").value("дБ"))
                .andExpect(jsonPath("$.code").doesNotExist());
    }
}
