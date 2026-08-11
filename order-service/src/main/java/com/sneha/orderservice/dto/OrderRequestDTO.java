package com.sneha.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class OrderRequestDTO {

    @NotBlank(message = "Item name cannot be empty")
    private String itemName;

    @Positive(message = "Quantity must be greater than zero")
    private int quantity;

    @Positive(message = "Price must be greater than zero")
    private double price;

    @NotBlank(message = "Status cannot be empty")
    private String status;

    public OrderRequestDTO() {
    }

    public OrderRequestDTO(String itemName, int quantity,
                           double price, String status) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
        this.status = status;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}