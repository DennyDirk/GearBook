package com.ds.booking_service.booking.dto;

import java.util.UUID;

public record CreateBookingRequest(
        UUID userId,
        Long inventoryId,
        Integer quantity
)
{
}
