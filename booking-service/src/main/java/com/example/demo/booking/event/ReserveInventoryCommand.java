package com.example.demo.booking.event;

import java.util.UUID;

public record ReserveInventoryCommand(
        UUID bookingId,
        Long inventoryId,
        Integer quantity
)
{
}
