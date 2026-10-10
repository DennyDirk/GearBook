package com.example.demo.booking.dto;

import java.util.UUID;

public record CreateBookingRequest(
        UUID userId,
        Long inventoryId,
        Integer quantity
)
{
}
