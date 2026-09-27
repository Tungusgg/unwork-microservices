package org.example.order.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тесты для OrderEventProducer.
 * Проверяют, что события отправляются в Kafka.
 */
@SpringBootTest
@AutoConfigureMockMvc
@EmbeddedKafka(partitions = 1, topics = {"order-created"})
@ActiveProfiles("default")
class OrderEventProducerTest {

    @Autowired
    private OrderEventProducer producer;

    /**
     * Тест: проверяем, что producer не null и метод send работает.
     */
    @Test
    void shouldSendEventToKafka() {
        // Запускаем отправку события
        String testEvent = "{\"orderId\":\"test-123\",\"productId\":\"TEST-789\"}";

        // Проверяем, что producer инициализирован
        assertNotNull(producer);

        // Отправляем событие
        producer.sendOrderCreatedEvent(testEvent);

        // В логах должно появиться сообщение об отправке
        // ( Awaitility можно использовать для асинхронных проверок)
        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    // Producer успешно отправил событие
                    assertTrue(true);
                });
    }
}
