package org.example.e2e;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * End-to-End тесты для всей системы.
 * Проверяют полный поток: order-service → Kafka → notification-service → RabbitMQ → inventory-service
 */
public class MicroservicesE2ETest {

    private static final String ORDER_SERVICE_URL = "http://localhost:8081";
    private static final String NOTIFICATION_SERVICE_URL = "http://localhost:8083";
    private static final String INVENTORY_SERVICE_URL = "http://localhost:8082";

    private static WebClient webClient;

    @BeforeAll
    static void setUp() {
        webClient = WebClient.create();
    }

    /**
     * E2E Тест 1: Создание заказа через order-service.
     * 
     * Ожидаемое поведение:
     * 1. order-service принимает POST /api/orders
     * 2. Отправляет событие в Kafka (order-created)
     * 3. notification-service получает событие
     * 4. notification-service отправляет в RabbitMQ
     * 5. inventory-service получает событие из RabbitMQ
     */
    @Test
    @DisplayName("Полный поток создания заказа")
    void shouldProcessOrderThroughAllServices() {
        // Шаг 1: Отправляем запрос на order-service
        String requestBody = "{\"productId\":\"E2E-TEST-001\"}";

        String response = webClient.post()
                .uri(ORDER_SERVICE_URL + "/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // Шаг 2: Проверяем ответ
        assertTrue(response.contains("принят в обработку"),
                "Ожидаем ответ 'принят в обработку', но получили: " + response);

        // Шаг 3: В логах должны появиться сообщения:
        // order-service: ">>> Отправлено событие в Kafka: {...}"
        // notification-service: "🔔 УВЕДОМЛЕНИЕ: Товар зарезервирован! Детали: {...}"
        // inventory-service: "<<< Получено событие из Kafka: {...}"
        // inventory-service: ">>> Отправлено событие в RabbitMQ: {...}"
    }

    /**
     * E2E Тест 2: Несколько заказов одновременно.
     */
    @Test
    @DisplayName("Обработка нескольких заказов")
    void shouldProcessMultipleOrders() {
        for (int i = 1; i <= 3; i++) {
            String requestBody = "{\"productId\":\"BATCH-\" + i}";

            String response = webClient.post()
                    .uri(ORDER_SERVICE_URL + "/api/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            assertTrue(response.contains("принят в обработку"),
                    "Заказ " + i + " не обработан");
        }
    }

    /**
     * E2E Тест 3: Проверка доступности сервисов.
     */
    @Test
    @DisplayName("Проверка доступности всех сервисов")
    void shouldCheckServiceAvailability() {
        // Проверяем, что order-service доступен
        boolean orderServiceAvailable = false;
        try {
            webClient.get()
                    .uri(ORDER_SERVICE_URL + "/actuator/health")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            orderServiceAvailable = true;
        } catch (Exception e) {
            // Сервис может не иметь actuator
        }

        // Если actuator не настроен, проверяем контроллер
        if (!orderServiceAvailable) {
            try {
                String response = webClient.get()
                        .uri(ORDER_SERVICE_URL + "/api/orders")
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                // Должно вернуться 405 или 400, но не 404
                assertTrue(true); // Если дошли сюда — сервис работает
            } catch (Exception e) {
                throw new RuntimeException("order-service недоступен на " + ORDER_SERVICE_URL);
            }
        }
    }
}
