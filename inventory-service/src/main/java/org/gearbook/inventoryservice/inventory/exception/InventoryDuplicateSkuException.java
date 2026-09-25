package org.gearbook.inventoryservice.inventory.exception;

public class InventoryDuplicateSkuException extends RuntimeException
{
    public InventoryDuplicateSkuException(String message)
    {
        super(String.format("Inventory with sku %s is already exists.", message));
    }
}
