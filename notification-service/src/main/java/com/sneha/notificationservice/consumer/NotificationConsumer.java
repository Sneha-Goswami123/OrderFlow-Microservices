package com.sneha.notificationservice.consumer;

import com.sneha.notificationservice.dto.OrderCreatedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

    @RabbitListener(queues = "notification.queue")
    public void handleNotification(OrderCreatedEvent event) {

        System.out.println("=================================");
        System.out.println("        NOTIFICATION SERVICE      ");
        System.out.println("=================================");
        System.out.println("Order ID: " + event.getOrderId());
        System.out.println("Item: " + event.getItemName());
        System.out.println("Quantity: " + event.getQuantity());
        System.out.println("Price: " + event.getPrice());
        System.out.println("Status: " + event.getStatus());
        System.out.println("Notification sent successfully!");
        System.out.println("=================================");
    }
}