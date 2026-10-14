package org.gearbook.inventoryservice.inventory.reservation;

import org.gearbook.inventoryservice.inventory.Inventory;
import org.gearbook.inventoryservice.inventory.InventoryRepository;
import org.gearbook.inventoryservice.inventory.event.InventoryRejectedEvent;
import org.gearbook.inventoryservice.inventory.event.InventoryReservedEvent;
import org.gearbook.inventoryservice.outbox.OutboxEvent;
import org.gearbook.inventoryservice.outbox.OutboxRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

@Service
public class ReservationService
{
    private final ReservationRepository reservationRepository;

    private final InventoryRepository inventoryRepository;

    private final JsonMapper jsonMapper;

    private final OutboxRepository outboxRepository;

    public ReservationService(ReservationRepository reservationRepository, InventoryRepository inventoryRepository,
                              JsonMapper jsonMapper, OutboxRepository outboxRepository)
    {
        this.reservationRepository = reservationRepository;
        this.inventoryRepository = inventoryRepository;
        this.jsonMapper = jsonMapper;
        this.outboxRepository = outboxRepository;
    }

    @Transactional
    public void reserve(UUID bookingId, Long inventoryId, Integer quantity)
    {
        if (quantity == null || quantity <= 0)
        {
            InventoryRejectedEvent rejectedEvent = new InventoryRejectedEvent(bookingId, inventoryId, quantity,
                    "Reservation quantity must be greater than zero");
            String payload = jsonMapper.writeValueAsString(rejectedEvent);
            OutboxEvent outboxEvent = new OutboxEvent(bookingId, "INVENTORY_REJECTED", payload);
            outboxRepository.save(outboxEvent);
            return;
        }

        int rowsUpdated = inventoryRepository.reserve(inventoryId, quantity);

        if (rowsUpdated == 0)
        {
            InventoryRejectedEvent rejectedEvent = new InventoryRejectedEvent(bookingId, inventoryId, quantity,
                    "No inventory available for reservation");
            String payload = jsonMapper.writeValueAsString(rejectedEvent);
            OutboxEvent outboxEvent = new OutboxEvent(bookingId, "INVENTORY_REJECTED", payload);
            outboxRepository.save(outboxEvent);
            return;
        }

        Inventory inventory = inventoryRepository.getReferenceById(inventoryId);

        Reservation reservation = new Reservation();
        reservation.setBookingId(bookingId);
        reservation.setQuantity(quantity);
        reservation.setInventory(inventory);
        reservation.setStatus(ReservationStatus.RESERVED);
        reservationRepository.save(reservation);

        String payload = jsonMapper.writeValueAsString(new InventoryReservedEvent(bookingId, inventoryId, quantity));
        OutboxEvent outboxEvent = new OutboxEvent(bookingId, "INVENTORY_RESERVED", payload);
        outboxRepository.save(outboxEvent);
    }
}
