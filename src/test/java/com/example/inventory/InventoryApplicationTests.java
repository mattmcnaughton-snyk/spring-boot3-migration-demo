package com.example.inventory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@code AutoConfigureMockMvc} moves to {@code org.springframework.boot.webmvc.test.autoconfigure}
 * in Spring Boot 4.
 */
@SpringBootTest
@AutoConfigureMockMvc
class InventoryApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void listsOnlyNonDeletedSeedProducts() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].price").value("9.99"));
    }

    @Test
    @WithMockUser
    void reportAggregatesStockAsynchronously() throws Exception {
        mockMvc.perform(get("/api/products/report").param("threshold", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lowStockCount").value(1));
    }

    @Test
    void productsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized());
    }
}
