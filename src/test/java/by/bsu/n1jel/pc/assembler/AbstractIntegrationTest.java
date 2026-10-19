package by.bsu.n1jel.pc.assembler;

import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Общая база интеграционных тестов: один Spring-контекст и одни контейнеры на все тесты.
 * Тесты не откатывают транзакции — каждый создаёт свои данные с уникальными именами,
 * поэтому проверяется реальное поведение, включая ленивую загрузку вне транзакций.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
public abstract class AbstractIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    protected static String unique(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    protected ResultActions postJson(String url, String json) throws Exception {
        return mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    protected ResultActions patchJson(String url, String json) throws Exception {
        return mockMvc.perform(patch(url).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    protected static String body(ResultActions result) throws Exception {
        return result.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    protected static Long readLong(ResultActions result, String path) throws Exception {
        Number value = JsonPath.read(body(result), path);
        return value.longValue();
    }

    /** id типа компонента из тестовых данных миграции v4 (например, "Процессор"). */
    protected Long seededComponentTypeId(String name) throws Exception {
        List<Number> ids = JsonPath.read(body(mockMvc.perform(get("/api/v1/components/types"))),
                "$[?(@.name == '%s')].id".formatted(name));
        return ids.getFirst().longValue();
    }

    // Фабрики тестовых данных через REST API

    protected Long createProducer(String name, String country) throws Exception {
        ResultActions result = postJson("/api/v1/producers/create", """
                {"name": "%s", "country": "%s"}
                """.formatted(name, country))
                .andExpect(status().isOk());
        return readLong(result, "$.id");
    }

    protected Long createSpecificationType(String name, String description) throws Exception {
        ResultActions result = postJson("/api/v1/specifications/types/create", """
                {"name": "%s", "description": "%s"}
                """.formatted(name, description))
                .andExpect(status().isOk());
        return readLong(result, "$.id");
    }

    protected Long createComponentType(String name) throws Exception {
        ResultActions result = postJson("/api/v1/components/types/create", """
                {"name": "%s"}
                """.formatted(name))
                .andExpect(status().isOk());
        return readLong(result, "$.id");
    }

    protected Long createComponent(String name, Long producerId, Long componentTypeId,
                                   String price, int stock, Long specTypeId, String specValue) throws Exception {
        ResultActions result = postJson("/api/v1/components/create", """
                {
                  "name": "%s",
                  "producer": %d,
                  "componentType": %d,
                  "price": %s,
                  "stockQuantity": %d,
                  "specifications": [{"specificationTypeId": %d, "value": "%s"}]
                }
                """.formatted(name, producerId, componentTypeId, price, stock, specTypeId, specValue))
                .andExpect(status().isOk());
        return readLong(result, "$.id");
    }

    protected Long createBuild(String name) throws Exception {
        ResultActions result = postJson("/api/v1/builds/create", """
                {"name": "%s"}
                """.formatted(name))
                .andExpect(status().isOk());
        return readLong(result, "$.buildId");
    }

    protected Long createPartition(Long buildId, Long componentId, int quantity) throws Exception {
        ResultActions result = postJson("/api/v1/builds/partitions/create", """
                {"buildId": %d, "componentId": %d, "quantity": %d}
                """.formatted(buildId, componentId, quantity))
                .andExpect(status().isOk());
        return readLong(result, "$.buildPartitionId");
    }
}
