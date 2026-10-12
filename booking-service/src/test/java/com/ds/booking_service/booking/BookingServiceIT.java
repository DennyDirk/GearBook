package com.ds.booking_service.booking;

import com.ds.booking_service.booking.dto.CreateBookingRequest;
import com.ds.booking_service.booking.event.ReserveInventoryCommand;
import com.ds.booking_service.outbox.OutboxEvent;
import com.ds.booking_service.outbox.OutboxRepository;
import com.ds.booking_service.outbox.OutboxStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

@SpringBootTest
@Testcontainers
public class BookingServiceIT
{
    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private OutboxRepository outboxRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Container
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:17-alpine");
    @Autowired
    private JsonMapper jsonMapper;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry)
    {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }


    @Test
    void shouldCreateBookingAndOutboxEventInSingleTransaction()
    {
        CreateBookingRequest createBookingRequest = new CreateBookingRequest(UUID.randomUUID(), 1500L, 2);
        Booking booking = bookingService.createBooking(createBookingRequest);
        Booking createdBooking = bookingRepository.findById(booking.getId()).orElseThrow();
        Assertions.assertEquals(BookingStatus.PENDING, createdBooking.getStatus());
        OutboxEvent outboxEvent = outboxRepository.findByAggregateId(createdBooking.getId()).orElseThrow();
        Assertions.assertEquals(booking.getId(), outboxEvent.getAggregateId());
        Assertions.assertEquals(OutboxStatus.NEW, outboxEvent.getStatus());
        Assertions.assertEquals("RESERVE_INVENTORY", outboxEvent.getEventType());
        Assertions.assertNotNull(outboxEvent.getPayload());
        ReserveInventoryCommand command = jsonMapper.readValue(outboxEvent.getPayload(), ReserveInventoryCommand.class);
        Assertions.assertEquals(command.bookingId(), booking.getId());
        Assertions.assertEquals(command.quantity(), booking.getQuantity());
        Assertions.assertEquals(command.inventoryId(), booking.getInventoryId());
    }

    @Test
    void shouldRollbackBookingWhenOutboxInsertFails()
    {
        long bookingsBefore = bookingRepository.count();
        long outboxBefore = outboxRepository.count();

        try
        {
            jdbcTemplate.execute(
                    "ALTER TABLE outbox ADD CONSTRAINT test_force_outbox_failure CHECK ( status <> 'NEW' )");

            CreateBookingRequest request = new CreateBookingRequest(UUID.randomUUID(), 1500L, 2);

            Assertions.assertThrows(
                    DataIntegrityViolationException.class,
                    () -> bookingService.createBooking(request)
            );

            Assertions.assertEquals(
                    bookingsBefore,
                    bookingRepository.count()
            );
            Assertions.assertEquals(
                    outboxBefore,
                    outboxRepository.count()
            );
        } finally
        {
            jdbcTemplate.execute("ALTER TABLE outbox DROP CONSTRAINT IF EXISTS test_force_outbox_failure");
        }
    }
}
