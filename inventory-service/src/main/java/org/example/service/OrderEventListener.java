package org.example.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderEventListener {

    private final InventoryEventProducer producer;

    public OrderEventListener(InventoryEventProducer producer) {
        this.producer = producer;
    }

    @KafkaListener(topics = "order-created", groupId = "inventory-group")
    public void handleOrderCreated(String message) {
        System.out.println("<<< Получено событие из Kafka: " + message);
        // Имитация резервирования товара
        String inventoryEvent = "{\"status\": \"RESERVED\", \"orderDetails\": " + message + "}";
        producer.sendInventoryReservedEvent(inventoryEvent);
    }
}