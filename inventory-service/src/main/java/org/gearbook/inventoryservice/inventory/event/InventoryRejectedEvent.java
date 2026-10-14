package org.gearbook.inventoryservice.inventory.event;

import java.util.UUID;

public record InventoryRejectedEvent(
        UUID bookingId,
        Long inventoryId,
        Integer quantity,
        String reason
)
{
}
