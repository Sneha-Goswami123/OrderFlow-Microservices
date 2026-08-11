package com.sneha.orderservice.controller;

import com.sneha.orderservice.model.Order;
import com.sneha.orderservice.service.OrderService;
import com.sneha.orderservice.dto.OrderResponseDTO;
import com.sneha.orderservice.dto.OrderRequestDTO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    // Constructor Injection
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // GET /orders
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {

        List<Order> orders = orderService.getAllOrders();
        List<OrderResponseDTO> response = new ArrayList<>();

        for (Order order : orders) {

            OrderResponseDTO dto = new OrderResponseDTO();

            dto.setId(order.getId());
            dto.setItemName(order.getItemName());
            dto.setQuantity(order.getQuantity());
            dto.setPrice(order.getPrice());
            dto.setStatus(order.getStatus());

            response.add(dto);
        }

        return ResponseEntity.ok(response);
    }

    // POST /orders
    @PostMapping
    public ResponseEntity<OrderResponseDTO> addOrder(
            @Valid @RequestBody OrderRequestDTO requestDTO) {

        Order order = new Order();
        order.setItemName(requestDTO.getItemName());
        order.setQuantity(requestDTO.getQuantity());
        order.setPrice(requestDTO.getPrice());
        order.setStatus(requestDTO.getStatus());

        Order savedOrder = orderService.addOrder(order);

        OrderResponseDTO responseDTO = new OrderResponseDTO();

        responseDTO.setId(savedOrder.getId());
        responseDTO.setItemName(savedOrder.getItemName());
        responseDTO.setQuantity(savedOrder.getQuantity());
        responseDTO.setPrice(savedOrder.getPrice());
        responseDTO.setStatus(savedOrder.getStatus());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {

        Order order = orderService.getOrderById(id);

        OrderResponseDTO dto = new OrderResponseDTO();

        dto.setId(order.getId());
        dto.setItemName(order.getItemName());
        dto.setQuantity(order.getQuantity());
        dto.setPrice(order.getPrice());
        dto.setStatus(order.getStatus());

        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id) {

        orderService.deleteOrder(id);

        return ResponseEntity.ok("Order deleted successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody OrderRequestDTO requestDTO) {

        Order order = new Order();

        order.setItemName(requestDTO.getItemName());
        order.setQuantity(requestDTO.getQuantity());
        order.setPrice(requestDTO.getPrice());
        order.setStatus(requestDTO.getStatus());

        Order updatedOrder = orderService.updateOrder(id, order);

        OrderResponseDTO responseDTO = new OrderResponseDTO();

        responseDTO.setId(updatedOrder.getId());
        responseDTO.setItemName(updatedOrder.getItemName());
        responseDTO.setQuantity(updatedOrder.getQuantity());
        responseDTO.setPrice(updatedOrder.getPrice());
        responseDTO.setStatus(updatedOrder.getStatus());

        return ResponseEntity.ok(responseDTO);
    }
}