package com.sneha.inventoryservice.service;

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
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
    }

    public Inventory getInventoryByItemName(String itemName) {
        return inventoryRepository.findByItemName(itemName)
                .orElseThrow(() -> new RuntimeException("Item not found"));
    }

    public Inventory updateInventory(Long id, Inventory updatedInventory) {

        Inventory inventory = getInventoryById(id);

        inventory.setItemName(updatedInventory.getItemName());
        inventory.setQuantity(updatedInventory.getQuantity());

        return inventoryRepository.save(inventory);
    }

    public void deleteInventory(Long id) {

        if (!inventoryRepository.existsById(id)) {
            throw new RuntimeException("Inventory not found");
        }

        inventoryRepository.deleteById(id);
    }
}