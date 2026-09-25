package org.gearbook.inventoryservice.inventory.exception;

public class InventoryInvalidQuantityException extends RuntimeException
{
    public InventoryInvalidQuantityException(Integer totalQuantity, Integer availableQuantity)
    {
        super("Available quantity: " + availableQuantity + " cannot be greater than total quantity: " + totalQuantity);
    }
}
