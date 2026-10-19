package by.bsu.n1jel.pc.assembler.api;

import by.bsu.n1jel.pc.assembler.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Полный цикл картинки через настоящий MinIO: загрузка → чтение → удаление. */
class ImageApiTest extends AbstractIntegrationTest {

    private static final String BASE = "/api/v1/images";

    @Test
    void uploadLoadDeleteRoundTrip() throws Exception {
        byte[] bytes = ("fake-png-" + unique("img")).getBytes(StandardCharsets.UTF_8);
        MockMultipartFile file = new MockMultipartFile("file", unique("photo") + ".png", "image/png", bytes);

        ResultActions upload = mockMvc.perform(multipart(BASE + "/upload")
                        .file(file)
                        .param("objectId", "1")
                        .param("objectType", "COMPONENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber());
        Long imageId = readLong(upload, "$.id");

        mockMvc.perform(get(BASE + "/{id}", imageId))
                .andExpect(status().isOk())
                .andExpect(content().string(Base64.getEncoder().encodeToString(bytes)));

        mockMvc.perform(delete(BASE + "/{id}", imageId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(imageId));

        mockMvc.perform(get(BASE + "/{id}", imageId))
                .andExpect(status().isNotFound());
    }

    @Test
    void producerBucketIsAvailable() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "logo.png", "image/png", new byte[]{1, 2, 3});

        mockMvc.perform(multipart(BASE + "/upload")
                        .file(file)
                        .param("objectId", "1")
                        .param("objectType", "PRODUCER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    void unknownImageReturns404() throws Exception {
        mockMvc.perform(get(BASE + "/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }
}
