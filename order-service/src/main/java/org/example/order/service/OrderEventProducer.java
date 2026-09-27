package org.example.order.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderEventProducer {
    private static final String TOPIC = "order-created";
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OrderEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrderCreatedEvent(String orderJson) {
        kafkaTemplate.send(TOPIC, orderJson);
        System.out.println(">>> Отправлено событие в Kafka: " + orderJson);
    }
}
