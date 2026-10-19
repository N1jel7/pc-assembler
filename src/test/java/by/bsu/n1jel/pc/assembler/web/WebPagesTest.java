package by.bsu.n1jel.pc.assembler.web;

import by.bsu.n1jel.pc.assembler.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Thymeleaf-страницы: рендерятся без ошибок на реальных данных, формы работают. */
class WebPagesTest extends AbstractIntegrationTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "/",
            "/builds", "/builds/create",
            "/components", "/components/create",
            "/producers", "/producers/create",
            "/specification-types", "/specification-types/create",
            "/swagger-ui/index.html", "/v3/api-docs"
    })
    void pageRenders(String url) throws Exception {
        mockMvc.perform(get(url))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/builds?query=Бюджет&priceFrom=100&priceTo=100000",
            "/components?query=Intel&inStock=true&priceFrom=1&priceTo=99999",
            "/producers?country=США",
            "/specification-types?query=Со",
            "/components?page=2"
    })
    void listPagesWithFilters(String url) throws Exception {
        mockMvc.perform(get(url))
                .andExpect(status().isOk());
    }

    @Test
    void indexShowsProjectName() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(content().string(containsString("PC ASSEMBLER")));
    }

    @Test
    void buildWizardFlow() throws Exception {
        Long producerId = createProducer(unique("Producer"), "США");
        Long specTypeId = createSpecificationType(unique("Spec"), "");
        Long cpuTypeId = seededComponentTypeId("Процессор");
        Long cpu1 = createComponent(unique("CPU"), producerId, cpuTypeId, "100.00", 5, specTypeId, "AM5");
        Long cpu2 = createComponent(unique("CPU"), producerId, cpuTypeId, "150.00", 5, specTypeId, "AM5");

        // создание через форму → редирект на редактирование
        MvcResult created = mockMvc.perform(post("/builds/create").param("name", unique("WebBuild")))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        String editUrl = created.getResponse().getRedirectedUrl();
        assertThat(editUrl).matches("/builds/\\d+/edit");
        Long buildId = Long.valueOf(editUrl.replaceAll("\\D", ""));

        mockMvc.perform(get(editUrl)).andExpect(status().isOk());
        mockMvc.perform(get("/components/select")
                        .param("buildId", buildId.toString())
                        .param("returnUrl", editUrl)
                        .param("componentType", cpuTypeId.toString()))
                .andExpect(status().isOk());

        // выбор процессора в слот
        mockMvc.perform(post("/builds/{id}/partitions/component", buildId)
                        .param("componentId", cpu1.toString())
                        .param("returnUrl", editUrl))
                .andExpect(redirectedUrl(editUrl));
        // повторный выбор в тот же слот заменяет компонент, а не добавляет второй
        mockMvc.perform(post("/builds/{id}/partitions/component", buildId)
                        .param("componentId", cpu2.toString())
                        .param("returnUrl", editUrl))
                .andExpect(redirectedUrl(editUrl));

        mockMvc.perform(get("/api/v1/builds/{id}", buildId))
                .andExpect(jsonPath("$.partitions", hasSize(1)))
                .andExpect(jsonPath("$.partitions[0].componentId").value(cpu2))
                .andExpect(jsonPath("$.price").value("150.00"));

        mockMvc.perform(get("/builds/{id}", buildId)).andExpect(status().isOk());
        mockMvc.perform(get(editUrl)).andExpect(status().isOk());

        mockMvc.perform(post("/builds/{id}/delete", buildId))
                .andExpect(redirectedUrl("/builds"));
        mockMvc.perform(get("/api/v1/builds/{id}", buildId))
                .andExpect(status().isNotFound());
    }

    @Test
    void componentPagesRender() throws Exception {
        Long producerId = createProducer(unique("Producer"), "США");
        Long specTypeId = createSpecificationType(unique("Spec"), "desc");
        Long id = createComponent(unique("GPU"), producerId, seededComponentTypeId("Видеокарта"),
                "500.00", 1, specTypeId, "16 GB");

        mockMvc.perform(get("/components/{id}", id)).andExpect(status().isOk());
        mockMvc.perform(get("/components/{id}/edit", id)).andExpect(status().isOk());

        mockMvc.perform(post("/components/{id}/edit", id).param("name", "Edited via form"))
                .andExpect(redirectedUrl("/components/" + id));
        mockMvc.perform(get("/api/v1/components/{id}", id))
                .andExpect(jsonPath("$.name").value("Edited via form"));
    }

    @Test
    void producerAndSpecificationTypeForms() throws Exception {
        String producerName = unique("FormProducer");
        mockMvc.perform(post("/producers/create").param("name", producerName).param("country", "Германия"))
                .andExpect(redirectedUrl("/producers"));
        mockMvc.perform(get("/producers").param("query", producerName))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(producerName)));

        Long specId = createSpecificationType(unique("FormSpec"), "desc");
        mockMvc.perform(get("/specification-types/{id}/edit", specId)).andExpect(status().isOk());
        mockMvc.perform(post("/specification-types/{id}/edit", specId).param("name", "Edited spec"))
                .andExpect(redirectedUrl("/specification-types"));
        mockMvc.perform(get("/api/v1/specifications/types/{id}", specId))
                .andExpect(jsonPath("$.name").value("Edited spec"))
                .andExpect(jsonPath("$.description").value("desc"));
    }

    @Test
    void seededEntitiesHaveWorkingDetailPages() throws Exception {
        Long seededBuildId = readLong(mockMvc.perform(get("/api/v1/builds")), "$[0].buildId");
        Long seededComponentId = readLong(mockMvc.perform(get("/api/v1/components")), "$[0].id");
        Long seededProducerId = readLong(mockMvc.perform(get("/api/v1/producers")), "$[0].id");

        mockMvc.perform(get("/builds/{id}", seededBuildId)).andExpect(status().isOk());
        mockMvc.perform(get("/builds/{id}/edit", seededBuildId)).andExpect(status().isOk());
        mockMvc.perform(get("/components/{id}", seededComponentId)).andExpect(status().isOk());
        mockMvc.perform(get("/producers/{id}/edit", seededProducerId)).andExpect(status().isOk());
    }
}
