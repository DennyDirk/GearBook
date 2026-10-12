package com.ds.booking_service.booking;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "booking")
public class Booking
{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "inventory_id", nullable = false)
    private Long inventoryId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "status", nullable = false, length = 16)
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    public Booking(UUID userId, Long inventoryId, Integer quantity)
    {
        this.userId = userId;
        this.inventoryId = inventoryId;
        this.quantity = quantity;
        this.status = BookingStatus.PENDING;
    }

    protected Booking()
    {

    }

    public UUID getId()
    {
        return id;
    }

    public BookingStatus getStatus()
    {
        return status;
    }

    public UUID getUserId()
    {
        return userId;
    }

    public Long getInventoryId()
    {
        return inventoryId;
    }

    public Integer getQuantity()
    {
        return quantity;
    }

    public Instant getCreatedAt()
    {
        return createdAt;
    }

    public void confirm()
    {
        this.status = BookingStatus.CONFIRMED;
    }

    public void reject()
    {
        this.status = BookingStatus.REJECTED;
    }
}
