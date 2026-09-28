package org.gearbook.inventoryservice.inventory.exception;

public class InventoryUnavailableException extends RuntimeException
{
    public InventoryUnavailableException(String message)
    {
        super(message);
    }
}
