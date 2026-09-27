package org.example.order.controller;

import org.example.order.dto.OrderRequest;
import org.example.order.service.OrderEventProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderEventProducer producer;

    public OrderController(OrderEventProducer producer) {
        this.producer = producer;
    }

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody OrderRequest request) {
        String event = String.format(
                "{\"orderId\": \"%s\", \"productId\": \"%s\"}",
                UUID.randomUUID(), request.getProductId()
        );
        producer.sendOrderCreatedEvent(event);
        return ResponseEntity.accepted().body("Заказ принят в обработку");
    }
}