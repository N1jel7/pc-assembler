package by.bsu.n1jel.pc.assembler.api;

import by.bsu.n1jel.pc.assembler.AbstractIntegrationTest;
import by.bsu.n1jel.pc.assembler.entity.Image;
import by.bsu.n1jel.pc.assembler.repository.ImageRepository;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Фото через настоящий MinIO: загрузка, привязка к сущности, отдача, удаление. */
class ImageApiTest extends AbstractIntegrationTest {

    private static final String BASE = "/api/v1/images";

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private MinioClient minioClient;

    private Long producerId;
    private Long componentId;

    @BeforeEach
    void setUp() throws Exception {
        producerId = createProducer(unique("Producer"), "США");
        componentId = createComponent(unique("GPU"), producerId, seededComponentTypeId("Видеокарта"),
                "100.00", 1, createSpecificationType(unique("Spec"), ""), "8 GB");
    }

    private static MockMultipartFile png(String content) {
        return new MockMultipartFile("file", "photo.png", "image/png", content.getBytes(StandardCharsets.UTF_8));
    }

    private ResultActions upload(MockMultipartFile file, String objectType, Long objectId) throws Exception {
        return mockMvc.perform(multipart(BASE + "/upload")
                .file(file)
                .param("objectId", objectId.toString())
                .param("objectType", objectType));
    }

    private boolean existsInStorage(Image image) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(image.getBucket()).object(image.getLocation()).build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Test
    void componentPhotoRoundTrip() throws Exception {
        String content = unique("png-bytes");
        Long imageId = readLong(upload(png(content), "COMPONENT", componentId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value(startsWith("/images/"))), "$.id");

        // привязано к компоненту
        mockMvc.perform(get("/api/v1/components/{id}", componentId))
                .andExpect(jsonPath("$.images", hasSize(1)))
                .andExpect(jsonPath("$.images[0].id").value(imageId))
                .andExpect(jsonPath("$.images[0].url").value("/images/" + imageId))
                // аватарка компонента остаётся SVG
                .andExpect(jsonPath("$.avatar").value(startsWith("data:image/svg+xml")));

        // файл отдаётся по ссылке с правильным типом
        mockMvc.perform(get("/images/{id}", imageId))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/png"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(content().bytes(content.getBytes(StandardCharsets.UTF_8)));

        // старый эндпоинт с base64 продолжает работать
        mockMvc.perform(get(BASE + "/{id}", imageId))
                .andExpect(content().string(Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8))));

        Image stored = imageRepository.findById(imageId).orElseThrow();
        mockMvc.perform(delete(BASE + "/{id}", imageId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/components/{id}", componentId))
                .andExpect(jsonPath("$.images", hasSize(0)));
        mockMvc.perform(get("/images/{id}", imageId))
                .andExpect(status().isNotFound());
        assertThat(existsInStorage(stored)).isFalse();
    }

    @Test
    void componentPhotoLimitIsFive() throws Exception {
        for (int i = 0; i < 5; i++) {
            upload(png("img" + i), "COMPONENT", componentId).andExpect(status().isOk());
        }
        upload(png("img6"), "COMPONENT", componentId)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void onlyRasterImagesAreAccepted() throws Exception {
        MockMultipartFile svg = new MockMultipartFile("file", "x.svg", "image/svg+xml",
                "<svg onload=alert(1)/>".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile text = new MockMultipartFile("file", "x.txt", "text/plain", new byte[]{1});

        upload(svg, "COMPONENT", componentId).andExpect(status().isBadRequest());
        upload(text, "COMPONENT", componentId).andExpect(status().isBadRequest());
    }

    @Test
    void producerPhotoBecomesAvatarAndIsReplaced() throws Exception {
        mockMvc.perform(get("/api/v1/producers/{id}", producerId))
                .andExpect(jsonPath("$.avatar").value(startsWith("data:image/svg+xml")))
                .andExpect(jsonPath("$.image").doesNotExist());

        Long first = readLong(upload(png("first"), "PRODUCER", producerId).andExpect(status().isOk()), "$.id");
        mockMvc.perform(get("/api/v1/producers/{id}", producerId))
                .andExpect(jsonPath("$.avatar").value("/images/" + first))
                .andExpect(jsonPath("$.image.id").value(first));

        Image firstStored = imageRepository.findById(first).orElseThrow();
        Long second = readLong(upload(png("second"), "PRODUCER", producerId).andExpect(status().isOk()), "$.id");

        mockMvc.perform(get("/api/v1/producers/{id}", producerId))
                .andExpect(jsonPath("$.avatar").value("/images/" + second));
        // старое фото удалено и из БД, и из MinIO
        mockMvc.perform(get("/images/{id}", first)).andExpect(status().isNotFound());
        assertThat(existsInStorage(firstStored)).isFalse();
    }

    @Test
    void buildPhotos() throws Exception {
        Long buildId = createBuild(unique("Build"));
        Long imageId = readLong(upload(png("build"), "BUILD", buildId).andExpect(status().isOk()), "$.id");

        mockMvc.perform(get("/api/v1/builds/{id}", buildId))
                .andExpect(jsonPath("$.images", hasSize(1)))
                .andExpect(jsonPath("$.images[0].url").value("/images/" + imageId));
    }

    @Test
    void deletingOwnerRemovesPhotosFromStorage() throws Exception {
        Long imageId = readLong(upload(png("to-delete"), "COMPONENT", componentId), "$.id");
        Image stored = imageRepository.findById(imageId).orElseThrow();
        assertThat(existsInStorage(stored)).isTrue();

        mockMvc.perform(delete("/api/v1/components/{id}", componentId))
                .andExpect(status().isOk());

        assertThat(imageRepository.findById(imageId)).isEmpty();
        assertThat(existsInStorage(stored)).isFalse();
    }

    @Test
    void uploadToUnknownObjectReturns404() throws Exception {
        upload(png("x"), "COMPONENT", Long.MAX_VALUE).andExpect(status().isNotFound());
        upload(png("x"), "PRODUCER", Long.MAX_VALUE).andExpect(status().isNotFound());
        upload(png("x"), "BUILD", Long.MAX_VALUE).andExpect(status().isNotFound());
    }

    @Test
    void unknownImageReturns404() throws Exception {
        mockMvc.perform(get(BASE + "/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/images/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }
}
