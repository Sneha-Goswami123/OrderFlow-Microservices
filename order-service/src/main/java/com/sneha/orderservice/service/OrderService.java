//package com.sneha.orderservice.service;
//
//import com.sneha.orderservice.model.Order;
//import org.springframework.stereotype.Service;
//import com.sneha.orderservice.exception.OrderNotFoundException;
//import com.sneha.orderservice.repository.OrderRepository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class OrderService {
//
//    private final OrderRepository orderRepository;
//
//    public OrderService(OrderRepository orderRepository) {
//        this.orderRepository = orderRepository;
//    }
//    public List<Order> getAllOrders() {
//        return orders;
//    }
//
//    public Order addOrder(Order order) {
//        orders.add(order);
//        return order;
//    }
//
//    public Order getOrderById(Long id){
//        for(Order order: orders){
//            if(order.getId().equals(id)){
//                return order;
//            }
//        }
//        throw new OrderNotFoundException("Order not found");
//    }
//
//    public boolean deleteOrder(Long id) {
//
//        for (Order order : orders) {
//
//            if (order.getId().equals(id)) {
//                orders.remove(order);
//                return true;
//            }
//
//        }
//
//        throw new OrderNotFoundException("Order not found");
//    }
//
//    public Order updateOrder(Long id, Order updatedOrder) {
//
//        for (Order order : orders) {
//
//            if (order.getId().equals(id)) {
//
//                order.setItemName(updatedOrder.getItemName());
//                order.setQuantity(updatedOrder.getQuantity());
//                order.setPrice(updatedOrder.getPrice());
//                order.setStatus(updatedOrder.getStatus());
//
//                return order;
//            }
//        }
//
//        throw new OrderNotFoundException("Order not found");
//    }
//}

package com.sneha.orderservice.service;

import com.sneha.orderservice.exception.OrderNotFoundException;
import com.sneha.orderservice.model.Order;
import com.sneha.orderservice.repository.OrderRepository;
import com.sneha.orderservice.producer.OrderProducer;
import com.sneha.orderservice.dto.OrderCreatedEvent;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;

    public OrderService(OrderRepository orderRepository, OrderProducer orderProducer) {
        this.orderRepository = orderRepository;
        this.orderProducer = orderProducer;
    }

    // Get All Orders
    public List<Order> getAllOrders() {

        logger.info("Fetching all orders");

        return orderRepository.findAll();
    }

    // Add Order
    public Order addOrder(Order order) {

        logger.info("Creating new order: {}", order.getItemName());

        // Save order in database
        Order savedOrder = orderRepository.save(order);

        // Create event
        OrderCreatedEvent event = new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getItemName(),
                savedOrder.getQuantity(),
                savedOrder.getPrice(),
                savedOrder.getStatus()
        );

        // Publish event
        orderProducer.sendOrderCreatedEvent(event);

        logger.info("Order event published successfully.");

        return savedOrder;
    }

    // Get Order By Id
    public Order getOrderById(Long id) {

        Optional<Order> order = orderRepository.findById(id);
        logger.info("Fetching order with ID {}", id);
        if (order.isPresent()) {
            return order.get();
        }
        logger.warn("Order with ID {} not found", id);
        throw new OrderNotFoundException("Order not found");
    }

    // Delete Order
    public boolean deleteOrder(Long id) {
        logger.warn("Delete failed. Order {} not found", id);
        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException("Order not found");
        }
        logger.info("Deleting order with ID {}", id);
        orderRepository.deleteById(id);

        return true;
    }

    // Update Order
    public Order updateOrder(Long id, Order updatedOrder) {

        Optional<Order> optionalOrder = orderRepository.findById(id);
        logger.info("Updating order with ID {}", id);
        if (optionalOrder.isPresent()) {

            Order order = optionalOrder.get();

            order.setItemName(updatedOrder.getItemName());
            order.setQuantity(updatedOrder.getQuantity());
            order.setPrice(updatedOrder.getPrice());
            order.setStatus(updatedOrder.getStatus());

            return orderRepository.save(order);
        }
        logger.warn("Update failed. Order {} not found", id);
        throw new OrderNotFoundException("Order not found");
    }
}