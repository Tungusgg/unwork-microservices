package org.example.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Интеграционные тесты для InventoryService.
 * Проверяют, что сервис получает события из Kafka и отправляет в RabbitMQ.
 */
@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"order-created"})
@ActiveProfiles("default")
class InventoryServiceTest {

    @Autowired
    private OrderEventListener orderListener;

    @Autowired
    private InventoryEventProducer producer;

    /**
     * Тест 1: Проверяем, что все бины созданы.
     */
    @Test
    void contextLoads() {
        assertNotNull(orderListener);
        assertNotNull(producer);
    }

    /**
     * Тест 2: Проверяем, что OrderEventListener обрабатывает события из Kafka.
     */
    @Test
    void shouldHandleOrderCreatedEvent() {
        String orderEvent = "{\"orderId\":\"INV-001\",\"productId\":\"TEST-PROD\"}";

        // Вызываем метод напрямую
        orderListener.handleOrderCreated(orderEvent);

        // TODO: В логах должно появиться:
        // 1. "<<< Получено событие из Kafka: ..."
        // 2. ">>> Отправлено событие в RabbitMQ: ..."
    }

    /**
     * Тест 3: Проверяем, что InventoryEventProducer отправляет события.
     */
    @Test
    void shouldSendInventoryReservedEvent() {
        String inventoryEvent = "{\"status\":\"RESERVED\",\"orderDetails\":{\"orderId\":\"TEST-1\",\"productId\":\"PROD-1\"}}";

        producer.sendInventoryReservedEvent(inventoryEvent);

        // Событие должно быть отправлено в RabbitMQ
        // В логах: ">>> Отправлено событие в RabbitMQ: ..."
    }
}
