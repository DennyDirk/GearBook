package org.gearbook.inventoryservice.inventory.event;

import java.util.UUID;

public record ReserveInventoryCommand(
        UUID bookingId,
        Long inventoryId,
        Integer quantity
)
{
}
