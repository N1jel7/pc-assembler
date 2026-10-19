package by.bsu.n1jel.pc.assembler.api;

import by.bsu.n1jel.pc.assembler.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SpecificationTypeApiTest extends AbstractIntegrationTest {

    private static final String BASE = "/api/v1/specifications/types";

    @Test
    void createReadEditDelete() throws Exception {
        String name = unique("Spec");
        Long id = createSpecificationType(name, "Описание");

        mockMvc.perform(get(BASE + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.description").value("Описание"));

        mockMvc.perform(get(BASE))
                .andExpect(jsonPath("$[*].name").value(hasItem(name)));

        patchJson(BASE + "/edit", """
                {"id": %d, "description": "Новое описание"}
                """.formatted(id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.description").value("Новое описание"));

        mockMvc.perform(delete(BASE + "/delete/{id}", id))
                .andExpect(status().isOk());
        mockMvc.perform(get(BASE + "/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void descriptionIsOptional() throws Exception {
        // В БД description nullable, значит без него тип тоже должен создаваться
        String name = unique("Spec");
        postJson(BASE + "/create", """
                {"name": "%s"}
                """.formatted(name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name));
    }
}
