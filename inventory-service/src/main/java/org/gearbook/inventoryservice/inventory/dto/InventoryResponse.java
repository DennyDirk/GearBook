package org.gearbook.inventoryservice.inventory.dto;

import org.gearbook.inventoryservice.inventory.Inventory;

import java.time.Instant;

public record InventoryResponse(Long id, String sku, String name, String description, Integer totalQuantity,
                                Integer availableQuantity, Instant createdAt, boolean active)
{
    public static InventoryResponse from(Inventory inventory)
    {
        return new InventoryResponse(inventory.getId(), inventory.getSku(), inventory.getName(),
                inventory.getDescription(), inventory.getTotalQuantity(), inventory.getAvailableQuantity(),
                inventory.getCreatedAt(),
                inventory.isActive());
    }
}
