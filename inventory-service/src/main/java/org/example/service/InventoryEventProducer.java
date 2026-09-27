package org.example.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class InventoryEventProducer {

    public static final String EXCHANGE = "inventory-exchange";
    public static final String ROUTING_KEY = "inventory.reserved";

    private final RabbitTemplate rabbitTemplate;

    public InventoryEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendInventoryReservedEvent(String message) {
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, message);
        System.out.println(">>> Отправлено событие в RabbitMQ: " + message);
    }
}