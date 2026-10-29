package com.example.inventory.web;

import com.example.inventory.domain.Product;
import com.example.inventory.service.ProductService;
import com.example.inventory.service.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Spring Boot 4 removes {@code @MockBean} (deprecated since 3.4) in favour of
 * {@code @MockitoBean}, and moves {@code @WebMvcTest} to
 * {@code org.springframework.boot.webmvc.test.autoconfigure}.
 */
@WebMvcTest(ProductController.class)
@SuppressWarnings("removal")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private ReportService reportService;

    @Test
    @WithMockUser
    void returnsAllProducts() throws Exception {
        given(productService.findAll())
                .willReturn(List.of(new Product("Widget", "Standard widget", new BigDecimal("9.99"), 10)));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Widget"));
    }

    @Test
    @WithMockUser
    void returns404ForUnknownProduct() throws Exception {
        given(productService.findById(42L)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/products/42"))
                .andExpect(status().isNotFound());
    }
}
