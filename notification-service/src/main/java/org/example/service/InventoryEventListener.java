package org.example.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class InventoryEventListener {

    @RabbitListener(queues = "inventory-reserved-queue")
    public void handleInventoryReserved(String message) {
        System.out.println("🔔 УВЕДОМЛЕНИЕ: Товар зарезервирован! Детали: " + message);
    }
}
