package by.bsu.n1jel.pc.assembler.api;

import by.bsu.n1jel.pc.assembler.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BuildApiTest extends AbstractIntegrationTest {

    private static final String BASE = "/api/v1/builds";

    private Long cpuId;
    private Long ramId;

    @BeforeEach
    void setUp() throws Exception {
        Long producerId = createProducer(unique("Producer"), "США");
        Long specTypeId = createSpecificationType(unique("Spec"), "");
        cpuId = createComponent(unique("CPU"), producerId, seededComponentTypeId("Процессор"),
                "100.50", 10, specTypeId, "AM5");
        ramId = createComponent(unique("RAM"), producerId, seededComponentTypeId("Оперативная память"),
                "40.25", 10, specTypeId, "DDR5");
    }

    @Test
    void newBuildIsEmptyAndFree() throws Exception {
        String name = unique("Build");
        Long id = createBuild(name);

        mockMvc.perform(get(BASE + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.price").value("0.00"))
                .andExpect(jsonPath("$.filledSlots").value(0))
                .andExpect(jsonPath("$.creationDate").isNotEmpty());
    }

    @Test
    void priceIsCalculatedFromPartitions() throws Exception {
        Long buildId = createBuild(unique("Build"));
        createPartition(buildId, cpuId, 1);
        Long ramPartitionId = createPartition(buildId, ramId, 2);

        // 100.50 * 1 + 40.25 * 2 = 181.00 — считает plpgsql-функция calculate_build_cost
        mockMvc.perform(get(BASE + "/{id}", buildId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value("181.00"))
                .andExpect(jsonPath("$.filledSlots").value(2))
                .andExpect(jsonPath("$.partitions", hasSize(2)));

        mockMvc.perform(get(BASE + "/partitions/{id}", ramPartitionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.buildId").value(buildId))
                .andExpect(jsonPath("$.componentId").value(ramId))
                .andExpect(jsonPath("$.componentTypeName").value("Оперативная память"))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.subtotal").value(80.50))
                // ограничения из component_types: ОЗУ 1..8, не фиксировано
                .andExpect(jsonPath("$.minQuantity").value(1))
                .andExpect(jsonPath("$.maxQuantity").value(8))
                .andExpect(jsonPath("$.fixedQuantity").value(false));
    }

    @Test
    void editingAndDeletingPartitionRecalculatesPrice() throws Exception {
        Long buildId = createBuild(unique("Build"));
        createPartition(buildId, cpuId, 1);
        Long ramPartitionId = createPartition(buildId, ramId, 1);

        patchJson(BASE + "/partitions/edit", """
                {"buildPartitionId": %d, "quantity": 4}
                """.formatted(ramPartitionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(4))
                .andExpect(jsonPath("$.componentId").value(ramId));

        mockMvc.perform(get(BASE + "/{id}", buildId))
                .andExpect(jsonPath("$.price").value("261.50"));

        mockMvc.perform(delete(BASE + "/partitions/{id}", ramPartitionId))
                .andExpect(status().isOk());

        mockMvc.perform(get(BASE + "/{id}", buildId))
                .andExpect(jsonPath("$.price").value("100.50"))
                .andExpect(jsonPath("$.filledSlots").value(1));
        mockMvc.perform(get(BASE + "/partitions/{id}", ramPartitionId))
                .andExpect(status().isNotFound());
    }

    @Test
    void renameBuild() throws Exception {
        Long buildId = createBuild(unique("Build"));
        String newName = unique("Renamed");

        patchJson(BASE + "/edit", """
                {"buildId": %d, "name": "%s"}
                """.formatted(buildId, newName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(newName));
    }

    @Test
    void deletingBuildRemovesItsPartitions() throws Exception {
        Long buildId = createBuild(unique("Build"));
        Long partitionId = createPartition(buildId, cpuId, 1);

        mockMvc.perform(delete(BASE + "/{id}", buildId))
                .andExpect(status().isOk());

        mockMvc.perform(get(BASE + "/{id}", buildId))
                .andExpect(status().isNotFound());
        mockMvc.perform(get(BASE + "/partitions/{id}", partitionId))
                .andExpect(status().isNotFound());
        // сам компонент при этом остаётся
        mockMvc.perform(get("/api/v1/components/{id}", cpuId))
                .andExpect(status().isOk());
    }

    @Test
    void seededBuildsHavePositivePrice() throws Exception {
        mockMvc.perform(get(BASE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == 'Бюджетный игровой ПК')].filledSlots").isNotEmpty());
    }

    @Test
    void unknownIdsReturn404() throws Exception {
        mockMvc.perform(get(BASE + "/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
        postJson(BASE + "/partitions/create", """
                {"buildId": %d, "componentId": %d, "quantity": 1}
                """.formatted(Long.MAX_VALUE, cpuId))
                .andExpect(status().isNotFound());
    }
}
