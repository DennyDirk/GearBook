package org.gearbook.inventoryservice.inventory;

import org.gearbook.inventoryservice.inventory.event.ReserveInventoryCommand;
import org.gearbook.inventoryservice.inventory.reservation.ReservationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class InventoryCommandListener
{
    private final JsonMapper jsonMapper;

    private final ReservationService reservationService;

    public InventoryCommandListener(JsonMapper jsonMapper, ReservationService reservationService)
    {
        this.jsonMapper = jsonMapper;
        this.reservationService = reservationService;
    }

    @KafkaListener(topics = "inventory.commands")
    public void consume(String payload)
    {
        ReserveInventoryCommand command = jsonMapper.readValue(payload, ReserveInventoryCommand.class);

        reservationService.reserve(command.bookingId(), command.inventoryId(), command.quantity());
    }
}
