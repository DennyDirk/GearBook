package org.gearbook.inventoryservice.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.gearbook.inventoryservice.inventory.Inventory;

public record InventoryRequest(@NotBlank @Size(max = 120) String sku,
                               @NotBlank @Size(max = 48) String name,
                               @Size(max = 256) String description,
                               @NotNull @PositiveOrZero Integer totalQuantity,
                               @NotNull @PositiveOrZero Integer availableQuantity,
                               boolean active)
{
    public static Inventory toInventory(InventoryRequest inventoryRequest)
    {
        Inventory inventory = new Inventory();
        inventory.setSku(inventoryRequest.sku);
        inventory.setName(inventoryRequest.name);
        inventory.setDescription(inventoryRequest.description);
        inventory.setTotalQuantity(inventoryRequest.totalQuantity);
        inventory.setAvailableQuantity(inventoryRequest.availableQuantity);
        inventory.setActive(inventoryRequest.active);
        return inventory;
    }
}
