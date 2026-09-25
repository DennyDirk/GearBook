package org.gearbook.inventoryservice.inventory.exception;

public class InventoryNotFoundException extends RuntimeException
{
    public InventoryNotFoundException(Long id)
    {
        super(String.format("Inventory with id %d not found", id));
    }
}
