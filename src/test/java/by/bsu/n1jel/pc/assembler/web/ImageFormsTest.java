package by.bsu.n1jel.pc.assembler.web;

import by.bsu.n1jel.pc.assembler.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Фото через веб-формы — так, как их отправляет браузер. */
class ImageFormsTest extends AbstractIntegrationTest {

    private Long producerId;
    private Long gpuTypeId;
    private Long specTypeId;

    @BeforeEach
    void setUp() throws Exception {
        producerId = createProducer(unique("Producer"), "США");
        gpuTypeId = seededComponentTypeId("Видеокарта");
        specTypeId = createSpecificationType(unique("VRAM"), "");
    }

    private static MockMultipartFile image(String name) {
        return new MockMultipartFile("images", name + ".png", "image/png", name.getBytes(StandardCharsets.UTF_8));
    }

    /** Браузер отправляет пустую часть, если файлы не выбраны. */
    private static MockMultipartFile noFile() {
        return new MockMultipartFile("images", "", "application/octet-stream", new byte[0]);
    }

    private Long idFromRedirect(MvcResult result) {
        return Long.valueOf(result.getResponse().getRedirectedUrl().replaceAll("\\D", ""));
    }

    @Test
    void componentCreateFormWithSpecificationsAndPhotos() throws Exception {
        String name = unique("GPU");
        MvcResult created = mockMvc.perform(multipart("/components/create")
                        .file(image("front"))
                        .file(image("back"))
                        .param("name", name)
                        .param("producerId", producerId.toString())
                        .param("componentTypeId", gpuTypeId.toString())
                        .param("price", "500")
                        .param("stockQuantity", "3")
                        .param("specifications[0].specificationTypeId", specTypeId.toString())
                        .param("specifications[0].value", "16 GB")
                        // пустая строка характеристики, добавленная и не заполненная
                        .param("specifications[1].specificationTypeId", "")
                        .param("specifications[1].value", ""))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        Long id = idFromRedirect(created);

        mockMvc.perform(get("/api/v1/components/{id}", id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.producerId").value(producerId))
                .andExpect(jsonPath("$.componentTypeId").value(gpuTypeId))
                .andExpect(jsonPath("$.specifications", hasSize(1)))
                .andExpect(jsonPath("$.specifications[0].value").value("16 GB"))
                .andExpect(jsonPath("$.images", hasSize(2)));

        mockMvc.perform(get("/components/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/images/")))
                // фото в одной ленте: 2 слайда, стрелки и миниатюры
                .andExpect(content().string(containsString("data-carousel")))
                .andExpect(content().string(containsString("data-carousel-next")))
                .andExpect(content().string(containsString("data-carousel-to=\"1\"")));
        mockMvc.perform(get("/components/{id}/edit", id))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("keptImageIds")));
    }

    @Test
    void componentEditFormKeepsRemovesAndAddsPhotos() throws Exception {
        MvcResult created = mockMvc.perform(multipart("/components/create")
                        .file(image("one"))
                        .file(image("two"))
                        .param("name", unique("GPU"))
                        .param("producerId", producerId.toString())
                        .param("componentTypeId", gpuTypeId.toString())
                        .param("price", "100")
                        .param("stockQuantity", "1")
                        .param("specifications[0].specificationTypeId", specTypeId.toString())
                        .param("specifications[0].value", "8 GB"))
                .andReturn();
        Long id = idFromRedirect(created);
        ResultActions before = mockMvc.perform(get("/api/v1/components/{id}", id));
        Long keptId = readLong(before, "$.images[0].id");
        Long specId = readLong(before, "$.specifications[0].id");

        mockMvc.perform(multipart("/components/{id}/edit", id)
                        .file(image("three"))
                        .param("name", "Edited")
                        .param("producerId", producerId.toString())
                        .param("componentTypeId", gpuTypeId.toString())
                        .param("price", "120")
                        .param("stockQuantity", "2")
                        .param("specifications[0].specificationTypeId", specTypeId.toString())
                        .param("specifications[0].value", "12 GB")
                        .param("keptImageIds", keptId.toString()))
                .andExpect(redirectedUrl("/components/" + id));

        mockMvc.perform(get("/api/v1/components/{id}", id))
                .andExpect(jsonPath("$.name").value("Edited"))
                .andExpect(jsonPath("$.stockQuantity").value(2))
                // характеристика того же типа обновлена, а не продублирована
                .andExpect(jsonPath("$.specifications", hasSize(1)))
                .andExpect(jsonPath("$.specifications[0].id").value(specId))
                .andExpect(jsonPath("$.specifications[0].value").value("12 GB"))
                .andExpect(jsonPath("$.images", hasSize(2)))
                .andExpect(jsonPath("$.images[0].id").value(keptId));
    }

    @Test
    void formIgnoresPhotosBeyondLimit() throws Exception {
        var request = multipart("/components/create")
                .param("name", unique("GPU"))
                .param("producerId", producerId.toString())
                .param("componentTypeId", gpuTypeId.toString())
                .param("price", "1")
                .param("stockQuantity", "1");
        for (int i = 0; i < 7; i++) {
            request.file(image("p" + i));
        }
        Long id = idFromRedirect(mockMvc.perform(request).andReturn());

        mockMvc.perform(get("/api/v1/components/{id}", id))
                .andExpect(jsonPath("$.images", hasSize(5)));
    }

    @Test
    void producerFormPhotoIsAvatar() throws Exception {
        String name = unique("FormProducer");
        mockMvc.perform(multipart("/producers/create")
                        .file(image("logo"))
                        .param("name", name)
                        .param("country", "Германия"))
                .andExpect(redirectedUrl("/producers"));

        java.util.List<Number> ids = com.jayway.jsonpath.JsonPath.read(body(mockMvc.perform(get("/api/v1/producers"))),
                "$[?(@.name == '%s')].id".formatted(name));
        Long id = ids.getFirst().longValue();
        ResultActions producer = mockMvc.perform(get("/api/v1/producers/{id}", id))
                .andExpect(jsonPath("$.avatar").value(startsWith("/images/")));
        String avatar = com.jayway.jsonpath.JsonPath.read(body(producer), "$.avatar");

        mockMvc.perform(get("/producers").param("query", name))
                .andExpect(content().string(containsString(avatar)));
        mockMvc.perform(get("/producers/{id}/edit", id))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(avatar)));

        // сняли галочку «Оставить» и не выбрали файл — фото удаляется, возвращается заготовка
        mockMvc.perform(multipart("/producers/{id}/edit", id)
                        .file(noFile())
                        .param("name", name)
                        .param("country", "Германия"))
                .andExpect(redirectedUrl("/producers"));
        mockMvc.perform(get("/api/v1/producers/{id}", id))
                .andExpect(jsonPath("$.avatar").value(startsWith("data:image/svg+xml")))
                .andExpect(jsonPath("$.image").doesNotExist());
        mockMvc.perform(get(avatar)).andExpect(status().isNotFound());
    }

    @Test
    void producerEditWithoutChangesKeepsPhoto() throws Exception {
        mockMvc.perform(multipart("/api/v1/images/upload")
                .file(new MockMultipartFile("file", "a.png", "image/png", new byte[]{1}))
                .param("objectId", producerId.toString())
                .param("objectType", "PRODUCER"));
        Long imageId = readLong(mockMvc.perform(get("/api/v1/producers/{id}", producerId)), "$.image.id");

        mockMvc.perform(multipart("/producers/{id}/edit", producerId)
                        .file(noFile())
                        .param("name", "Renamed producer")
                        .param("country", "США")
                        .param("keptImageIds", imageId.toString()))
                .andExpect(redirectedUrl("/producers"));

        mockMvc.perform(get("/api/v1/producers/{id}", producerId))
                .andExpect(jsonPath("$.name").value("Renamed producer"))
                .andExpect(jsonPath("$.image.id").value(imageId));
    }

    @Test
    void buildFormPhotos() throws Exception {
        MvcResult created = mockMvc.perform(multipart("/builds/create")
                        .file(image("case"))
                        .param("name", unique("Build")))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        Long buildId = idFromRedirect(created);

        mockMvc.perform(get("/api/v1/builds/{id}", buildId))
                .andExpect(jsonPath("$.images", hasSize(1)));

        mockMvc.perform(multipart("/builds/{id}/edit", buildId)
                        .file(image("inside"))
                        .param("name", "Build with photos")
                        .param("keptImageIds", readLong(mockMvc.perform(get("/api/v1/builds/{id}", buildId)), "$.images[0].id").toString()))
                .andExpect(redirectedUrl("/builds/" + buildId));

        mockMvc.perform(get("/api/v1/builds/{id}", buildId))
                .andExpect(jsonPath("$.images", hasSize(2)));
        mockMvc.perform(get("/builds/{id}", buildId))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/images/")));
        mockMvc.perform(get("/builds/{id}/edit", buildId))
                .andExpect(status().isOk());
    }
}
