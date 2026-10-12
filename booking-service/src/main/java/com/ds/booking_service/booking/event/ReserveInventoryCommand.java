package com.ds.booking_service.booking.event;

import java.util.UUID;

public record ReserveInventoryCommand(
        UUID bookingId,
        Long inventoryId,
        Integer quantity
)
{
}
