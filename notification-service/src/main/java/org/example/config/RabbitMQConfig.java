package org.example.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue inventoryReservedQueue() {
        return new Queue("inventory-reserved-queue", true);
    }
}
