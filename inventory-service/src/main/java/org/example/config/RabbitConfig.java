package org.example.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "inventory-exchange";
    public static final String QUEUE = "inventory-reserved-queue";
    public static final String ROUTING_KEY = "inventory.reserved";

    @Bean
    public DirectExchange inventoryExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue inventoryReservedQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public Binding inventoryBinding(Queue inventoryReservedQueue, DirectExchange inventoryExchange) {
        return BindingBuilder.bind(inventoryReservedQueue).to(inventoryExchange).with(ROUTING_KEY);
    }
}