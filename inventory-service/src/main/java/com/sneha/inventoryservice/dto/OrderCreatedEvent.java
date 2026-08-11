package com.sneha.inventoryservice.dto;

public class OrderCreatedEvent {

    private Long id;
    private String itemName;
    private int quantity;
    private double price;
    private String status;

    public OrderCreatedEvent() {
    }

    public OrderCreatedEvent(Long id, String itemName, int quantity,
                             double price, String status) {
        this.id = id;
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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