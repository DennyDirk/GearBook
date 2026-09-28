package org.gearbook.inventoryservice.inventory.reservation;

import org.gearbook.inventoryservice.inventory.Inventory;
import org.gearbook.inventoryservice.inventory.InventoryRepository;
import org.gearbook.inventoryservice.inventory.exception.InventoryUnavailableException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ReservationService
{
    private final ReservationRepository reservationRepository;

    private final InventoryRepository inventoryRepository;

    public ReservationService(ReservationRepository reservationRepository, InventoryRepository inventoryRepository)
    {
        this.reservationRepository = reservationRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public Reservation reserve(UUID bookingId, Long inventoryId, Integer quantity)
    {
        if (quantity == null || quantity <= 0)
        {
            throw new IllegalArgumentException(
                    "Reservation quantity must be greater than zero"
            );
        }

        int rowsUpdated = inventoryRepository.reserve(inventoryId, quantity);

        if (rowsUpdated == 0)
        {
            throw new InventoryUnavailableException("No inventory available for reservation");
        }

        Inventory inventory = inventoryRepository.getReferenceById(inventoryId);

        Reservation reservation = new Reservation();
        reservation.setBookingId(bookingId);
        reservation.setQuantity(quantity);
        reservation.setInventory(inventory);
        reservation.setStatus(ReservationStatus.RESERVED);

        return reservationRepository.save(reservation);
    }
}
