package com.sneha.inventoryservice.controller;

import com.sneha.inventoryservice.model.Inventory;
import com.sneha.inventoryservice.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<List<Inventory>> getAllInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    @PostMapping
    public ResponseEntity<Inventory> addInventory(
            @Valid @RequestBody Inventory inventory) {

        return ResponseEntity.status(201)
                .body(inventoryService.addInventory(inventory));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inventory> getInventoryById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                inventoryService.getInventoryById(id)
        );
    }

    @GetMapping("/item/{itemName}")
    public ResponseEntity<Inventory> getInventoryByItemName(
            @PathVariable String itemName) {

        return ResponseEntity.ok(
                inventoryService.getInventoryByItemName(itemName)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inventory> updateInventory(
            @PathVariable Long id,
            @Valid @RequestBody Inventory inventory) {

        return ResponseEntity.ok(
                inventoryService.updateInventory(id, inventory)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInventory(
            @PathVariable Long id) {

        inventoryService.deleteInventory(id);

        return ResponseEntity.ok("Inventory deleted successfully");
    }

    @GetMapping("/test")
    public String test() {
        return "Inventory Service is working";
    }
}