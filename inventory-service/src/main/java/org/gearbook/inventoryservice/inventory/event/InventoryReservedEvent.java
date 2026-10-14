package org.gearbook.inventoryservice.inventory.event;

import java.util.UUID;

public record InventoryReservedEvent(
        UUID bookingId,
        Long inventoryId,
        Integer quantity
)
{
}
