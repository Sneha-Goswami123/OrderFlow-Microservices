package com.sneha.notificationservice.dto;

public class NotificationEvent {

    private String itemName;
    private int quantity;

    public NotificationEvent() {
    }

    public NotificationEvent(String itemName, int quantity) {
        this.itemName = itemName;
        this.quantity = quantity;
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
}