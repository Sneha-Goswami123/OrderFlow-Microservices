package com.sneha.inventoryservice.consumer;

import com.sneha.inventoryservice.config.RabbitMQConfig;
import com.sneha.inventoryservice.dto.OrderCreatedEvent;
import com.sneha.inventoryservice.service.InventoryService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryConsumer {

    private final InventoryService inventoryService;
    private final RabbitTemplate rabbitTemplate;

    public InventoryConsumer(
            InventoryService inventoryService,
            RabbitTemplate rabbitTemplate) {

        this.inventoryService = inventoryService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "order.queue")
    public void handleOrderCreated(OrderCreatedEvent event) {

        System.out.println(
                "Received order event for: " + event.getItemName()
        );

        // Reduce inventory stock
        inventoryService.reduceStock(
                event.getItemName(),
                event.getQuantity()
        );

        // Publish event to Notification Service
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_QUEUE,
                event
        );

        System.out.println(
                "Notification event sent for: " + event.getItemName()
        );
    }
}