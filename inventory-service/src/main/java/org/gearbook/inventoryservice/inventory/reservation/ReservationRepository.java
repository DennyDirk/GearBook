package org.gearbook.inventoryservice.inventory.reservation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, Long>
{
    boolean existsByBookingId(UUID bookingId);

    int countByInventory_Id(Long inventoryId);
}
