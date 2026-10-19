package by.bsu.n1jel.pc.assembler.api;

import by.bsu.n1jel.pc.assembler.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProducerApiTest extends AbstractIntegrationTest {

    @Test
    void createdProducerCanBeReadBack() throws Exception {
        String name = unique("Producer");
        Long id = createProducer(name, "Беларусь");

        mockMvc.perform(get("/api/v1/producers/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.country").value("Беларусь"))
                .andExpect(jsonPath("$.avatar").value(startsWith("data:image/svg+xml")));

        mockMvc.perform(get("/api/v1/producers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name").value(hasItem(name)));
    }

    @Test
    void partialEditKeepsUntouchedFields() throws Exception {
        Long id = createProducer(unique("Producer"), "Польша");
        String newName = unique("Renamed");

        patchJson("/api/v1/producers/edit", """
                {"id": %d, "name": "%s"}
                """.formatted(id, newName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(newName))
                .andExpect(jsonPath("$.country").value("Польша"));
    }

    @Test
    void deletedProducerIsNotFound() throws Exception {
        Long id = createProducer(unique("Producer"), "Литва");

        mockMvc.perform(delete("/api/v1/producers/delete/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

        mockMvc.perform(get("/api/v1/producers/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.description").value("Producer id = %d not found".formatted(id)));
    }

    @Test
    void unknownProducerReturns404() throws Exception {
        mockMvc.perform(get("/api/v1/producers/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
        patchJson("/api/v1/producers/edit", """
                {"id": %d, "name": "x"}
                """.formatted(Long.MAX_VALUE))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/v1/producers/delete/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }
}
