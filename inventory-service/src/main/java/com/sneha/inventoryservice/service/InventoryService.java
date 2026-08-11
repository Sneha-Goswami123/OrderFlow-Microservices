package com.sneha.inventoryservice.service;

import com.sneha.inventoryservice.exception.InventoryNotFoundException;
import com.sneha.inventoryservice.model.Inventory;
import com.sneha.inventoryservice.repository.InventoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public Inventory addInventory(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    public Inventory getInventoryById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() ->
                        new InventoryNotFoundException(
                                "Inventory not found with id: " + id
                        )
                );
    }

    public Inventory getInventoryByItemName(String itemName) {
        return inventoryRepository.findByItemName(itemName)
                .orElseThrow(() ->
                        new InventoryNotFoundException(
                                "Inventory not found for item: " + itemName
                        )
                );
    }

    public Inventory updateInventory(Long id, Inventory updatedInventory) {

        Inventory inventory = getInventoryById(id);

        inventory.setItemName(updatedInventory.getItemName());
        inventory.setQuantity(updatedInventory.getQuantity());

        return inventoryRepository.save(inventory);
    }

    public void deleteInventory(Long id) {

        if (!inventoryRepository.existsById(id)) {
            throw new InventoryNotFoundException(
                    "Inventory not found with id: " + id
            );
        }

        inventoryRepository.deleteById(id);
    }

    public Inventory reduceStock(String itemName, int quantity) {

        Inventory inventory = inventoryRepository.findByItemName(itemName)
                .orElseThrow(() ->
                        new InventoryNotFoundException(
                                "Inventory not found for item: " + itemName
                        )
                );

        if (inventory.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        inventory.setQuantity(
                inventory.getQuantity() - quantity
        );

        return inventoryRepository.save(inventory);
    }
}