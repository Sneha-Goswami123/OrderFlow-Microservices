package com.sneha.inventoryservice.consumer;

import com.sneha.inventoryservice.dto.OrderCreatedEvent;
import com.sneha.inventoryservice.service.InventoryService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryConsumer {

    private final InventoryService inventoryService;

    public InventoryConsumer(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @RabbitListener(queues = "order.queue")
    public void handleOrderCreated(OrderCreatedEvent event) {

        System.out.println(
                "Received order event for: " + event.getItemName()
        );

        inventoryService.reduceStock(
                event.getItemName(),
                event.getQuantity()
        );
    }
}