package org.example.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Интеграционные тесты для NotificationService.
 * Проверяют, что сервис запускается и RabbitMQ подключен.
 */
@SpringBootTest
@ActiveProfiles("default")
class NotificationServiceTest {

    @Autowired
    private InventoryEventListener listener;

    /**
     * Тест 1: Проверяем, что listenerbean создан.
     */
    @Test
    void contextLoads() {
        assertNotNull(listener);
    }

    /**
     * Тест 2: Проверяем, что метод handleInventoryReserved работает.
     */
    @Test
    void shouldHandleInventoryReservedEvent() {
        // Вызываем метод напрямую
        String testMessage = "{\"orderId\":\"test-123\",\"productId\":\"PROD-456\"}";

        // Метод должен выполниться без исключений
        listener.handleInventoryReserved(testMessage);

        // TODO: В консоли должно появиться сообщение
        // "🔔 УВЕДОМЛЕНИЕ: Товар зарезервирован! Детали: ..."
    }
}
