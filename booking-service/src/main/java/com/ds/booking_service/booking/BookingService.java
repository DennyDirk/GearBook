package com.ds.booking_service.booking;

import com.ds.booking_service.booking.dto.CreateBookingRequest;
import com.ds.booking_service.booking.event.ReserveInventoryCommand;
import com.ds.booking_service.outbox.OutboxEvent;
import com.ds.booking_service.outbox.OutboxRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

@Service
public class BookingService
{
    private final OutboxRepository outboxRepository;

    private final BookingRepository bookingRepository;

    private final JsonMapper jsonMapper;

    public BookingService(OutboxRepository outboxRepository, BookingRepository bookingRepository, JsonMapper jsonMapper)
    {
        this.outboxRepository = outboxRepository;
        this.bookingRepository = bookingRepository;
        this.jsonMapper = jsonMapper;
    }

    @Transactional
    public Booking createBooking(CreateBookingRequest request)
    {
        Booking booking = new Booking(request.userId(), request.inventoryId(), request.quantity());
        bookingRepository.save(booking);
        ReserveInventoryCommand command =
                new ReserveInventoryCommand(booking.getId(), booking.getInventoryId(), booking.getQuantity());
        String payload = jsonMapper.writeValueAsString(command);
        OutboxEvent outboxEvent = new OutboxEvent(booking.getId(), "RESERVE_INVENTORY", payload);
        outboxRepository.save(outboxEvent);
        return booking;

    }


}
;