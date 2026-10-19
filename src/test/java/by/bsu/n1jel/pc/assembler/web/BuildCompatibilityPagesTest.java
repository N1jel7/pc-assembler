package by.bsu.n1jel.pc.assembler.web;

import by.bsu.n1jel.pc.assembler.AbstractIntegrationTest;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Количество в слотах и отображение совместимости на страницах сборки. */
class BuildCompatibilityPagesTest extends AbstractIntegrationTest {

    private Long componentId(String name) throws Exception {
        List<Number> ids = JsonPath.read(body(mockMvc.perform(get("/api/v1/components"))),
                "$[?(@.name == '%s')].id".formatted(name));
        return ids.getFirst().longValue();
    }

    @Test
    void quantityIsSavedFromBuildForm() throws Exception {
        Long buildId = createBuild(unique("Qty"));
        Long ramPartition = createPartition(buildId, componentId("Samsung DDR5 16GB"), 1);

        mockMvc.perform(get("/builds/{id}/edit", buildId))
                .andExpect(content().string(containsString("name=\"quantities[" + ramPartition + "]\"")));

        mockMvc.perform(post("/builds/{id}/edit", buildId)
                        .param("name", "С двумя планками")
                        .param("quantities[" + ramPartition + "]", "2"))
                .andExpect(redirectedUrl("/builds/" + buildId));

        mockMvc.perform(get("/api/v1/builds/partitions/{id}", ramPartition))
                .andExpect(jsonPath("$.quantity").value(2));
        mockMvc.perform(get("/api/v1/builds/{id}", buildId))
                .andExpect(jsonPath("$.name").value("С двумя планками"));
    }

    @Test
    void quantityOutOfRangeIsRejected() throws Exception {
        Long buildId = createBuild(unique("Qty"));
        Long ramPartition = createPartition(buildId, componentId("Samsung DDR5 16GB"), 1);
        Long cpuPartition = createPartition(buildId, componentId("AMD Ryzen 5 7600X"), 1);

        // форма: возврат на страницу с сообщением
        mockMvc.perform(post("/builds/{id}/edit", buildId)
                        .header("Referer", "http://localhost/builds/" + buildId + "/edit")
                        .param("name", "x")
                        .param("quantities[" + ramPartition + "]", "9"))
                .andExpect(status().isSeeOther())
                .andExpect(redirectedUrl("/builds/" + buildId + "/edit"))
                .andExpect(flash().attribute("uploadError", "«Оперативная память»: количество должно быть от 1 до 8"));
        mockMvc.perform(get("/api/v1/builds/partitions/{id}", ramPartition))
                .andExpect(jsonPath("$.quantity").value(1));

        // REST
        patchJson("/api/v1/builds/partitions/edit", """
                {"buildPartitionId": %d, "quantity": 2}
                """.formatted(cpuPartition))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("«Процессор»: в сборке может быть только 1 шт."));
        postJson("/api/v1/builds/partitions/create", """
                {"buildId": %d, "componentId": %d, "quantity": 0}
                """.formatted(createBuild(unique("Qty")), componentId("Samsung DDR5 16GB")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void partitionOfAnotherBuildCannotBeChanged() throws Exception {
        Long foreignPartition = createPartition(createBuild(unique("Other")), componentId("Samsung DDR5 16GB"), 1);
        Long buildId = createBuild(unique("Mine"));

        patchJson("/api/v1/builds/edit", """
                {"buildId": %d, "quantities": {"%d": 2}}
                """.formatted(buildId, foreignPartition))
                .andExpect(status().isBadRequest());
    }

    @Test
    void restEditWithoutNameKeepsName() throws Exception {
        String name = unique("Keep");
        Long buildId = createBuild(name);
        Long ramPartition = createPartition(buildId, componentId("Samsung DDR5 16GB"), 1);

        patchJson("/api/v1/builds/edit", """
                {"buildId": %d, "quantities": {"%d": 4}}
                """.formatted(buildId, ramPartition))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    void buildPagesShowCompatibility() throws Exception {
        Long buildId = createBuild(unique("Broken"));
        createPartition(buildId, componentId("AMD Ryzen 9 7950X"), 1);
        createPartition(buildId, componentId("MSI PRO Z790-A WiFi"), 1);

        for (String page : List.of("/builds/" + buildId, "/builds/" + buildId + "/edit")) {
            mockMvc.perform(get(page))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Есть несовместимости")))
                    .andExpect(content().string(containsString(
                            "Процессор с сокетом AM5 не встанет в материнскую плату с сокетом LGA1700")))
                    .andExpect(content().string(containsString("slot-card--error")));
        }

        mockMvc.perform(get("/builds").param("query", "Broken"))
                .andExpect(content().string(containsString("✕ Несовместимо")));
    }

    @Test
    void compatibleBuildPage() throws Exception {
        Long buildId = createBuild(unique("Good"));
        for (String name : List.of("AMD Ryzen 7 7800X3D", "MSI MPG B650 Carbon WiFi", "Corsair Vengeance DDR5 32GB",
                "NVIDIA RTX 4070 Ti", "Samsung 990 Pro 1TB", "Corsair RM850x 850W",
                "Corsair 5000D Airflow", "Deepcool AK620")) {
            createPartition(buildId, componentId(name), 1);
        }

        mockMvc.perform(get("/builds/{id}", buildId))
                .andExpect(content().string(containsString("Всё совместимо")))
                .andExpect(content().string(not(containsString("slot-card--error"))));
        mockMvc.perform(get("/builds").param("query", "Good"))
                .andExpect(content().string(containsString("✓ Совместима")));
    }

    @Test
    void componentSelectionShowsCompatibleFirst() throws Exception {
        Long buildId = createBuild(unique("Select"));
        createPartition(buildId, componentId("AMD Ryzen 7 7800X3D"), 1);
        Long boardType = seededComponentTypeId("Материнская плата");
        String returnUrl = "/builds/" + buildId + "/edit";

        String page = body(mockMvc.perform(get("/components/select")
                        .param("buildId", buildId.toString())
                        .param("componentType", boardType.toString())
                        .param("returnUrl", returnUrl)
                        .param("query", "MSI"))
                .andExpect(status().isOk()));

        // B650 (AM5) совместима, Z790 (LGA1700) — нет, и она ниже
        assertThat(page).contains("✓ Совместим со сборкой", "Выбрать всё равно",
                "Процессор с сокетом AM5 не встанет в материнскую плату с сокетом LGA1700");
        assertThat(page.indexOf("MSI MPG B650 Carbon WiFi")).isLessThan(page.indexOf("MSI PRO Z790-A WiFi"));

        // поиск остаётся в режиме выбора
        assertThat(page).contains("action=\"/components/select\"", "name=\"buildId\"");
    }
}
