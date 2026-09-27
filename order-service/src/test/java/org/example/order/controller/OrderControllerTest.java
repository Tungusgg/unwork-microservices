package org.example.order.controller;

import org.example.order.dto.OrderRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Интеграционные тесты для OrderController.
 * Проверяют, что контроллер принимает запросы и возвращает правильные ответы.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("default")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Тест 1: Успешное создание заказа.
     * Проверяем, что POST /api/orders возвращает 202 Accepted.
     */
    @Test
    void shouldCreateOrderSuccessfully() throws Exception {
        OrderRequest request = new OrderRequest("PROD-123");

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":\"PROD-123\"}"))
                .andExpect(status().isAccepted()) // 202
                .andExpect(content().string(containsString("принят в обработку")));
    }

    /**
     * Тест 2: Проверка, что сервис принимает заказы с любым productId.
     */
    @Test
    void shouldAcceptAnyProductId() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":\"\"}"))
                .andExpect(status().isAccepted()); // 202
    }

    /**
     * Тест 3: Проверка, что сервис возвращает текст.
     */
    @Test
    void shouldReturnTextResponse() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":\"TEST-456\"}"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN));
    }
}
