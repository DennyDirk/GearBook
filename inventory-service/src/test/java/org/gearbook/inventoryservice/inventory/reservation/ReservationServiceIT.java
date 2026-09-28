package org.gearbook.inventoryservice.inventory.reservation;

import org.gearbook.inventoryservice.inventory.Inventory;
import org.gearbook.inventoryservice.inventory.InventoryRepository;
import org.gearbook.inventoryservice.inventory.exception.InventoryUnavailableException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

@SpringBootTest
@Testcontainers
public class ReservationServiceIT
{
    @Container
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:17-alpine");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry)
    {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Test
    void contextLoads()
    {
    }

    @Test
    void shouldAllowOnlyOneReservationWhenOneItemLeft() throws InterruptedException, ExecutionException
    {
        Inventory inventory = new Inventory();
        inventory.setSku("a-c12-33-fc1");
        inventory.setActive(true);
        inventory.setName("Canon Flashlight");
        inventory.setTotalQuantity(3);
        inventory.setAvailableQuantity(1);
        inventory.setDescription("Powerful flashlight");
        inventoryRepository.saveAndFlush(inventory);

        UUID bookingId1 = UUID.randomUUID();
        UUID bookingId2 = UUID.randomUUID();

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        try {

            Callable<Boolean> task1 = () -> {
            ready.countDown();
            start.await();
            try {
                reservationService.reserve(bookingId1, inventory.getId(), 1);
                return true;
            } catch (InventoryUnavailableException ex) {
                return false;
            }
            };

            Callable<Boolean> task2 = () -> {
            ready.countDown();
            start.await();
            try {
                reservationService.reserve(bookingId2, inventory.getId(), 1);
                return true;
            } catch (InventoryUnavailableException ex) {
                return false;
            }
            };

            Future<Boolean> result1 = executorService.submit(task1);
            Future<Boolean> result2 = executorService.submit(task2);

            ready.await();
            start.countDown();

            boolean firstSucceeded = result1.get();
            boolean secondSucceeded = result2.get();

            long successes = Stream.of(firstSucceeded, secondSucceeded).filter(Boolean::booleanValue).count();

            Assertions.assertEquals(1, successes);

            Inventory updatedInventory = inventoryRepository.findById(inventory.getId()).orElseThrow();

            Assertions.assertEquals(0, updatedInventory.getAvailableQuantity());

            Assertions.assertEquals(1, reservationRepository.countByInventory_Id(inventory.getId()));
        }
        finally
        {
            executorService.shutdown();
        }
    }

    @Test
    void shouldRollbackInventoryUpdateWhenReservationInsertFails()
    {
        Inventory inventory = new Inventory();
        inventory.setSku("test-sku-" + UUID.randomUUID());
        inventory.setActive(true);
        inventory.setName("Canon Flashlight");
        inventory.setTotalQuantity(2);
        inventory.setAvailableQuantity(2);
        inventory.setDescription("Powerful flashlight");
        inventoryRepository.saveAndFlush(inventory);

        UUID bookingId = UUID.randomUUID();

        reservationService.reserve(bookingId, inventory.getId(), 1);
        Assertions.assertEquals(1, reservationRepository.countByInventory_Id(inventory.getId()));

        Inventory afterReservation = inventoryRepository.findById(inventory.getId()).orElseThrow();

        Assertions.assertEquals(1, afterReservation.getAvailableQuantity());

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            reservationService.reserve(bookingId, inventory.getId(), 1);
        });

        Inventory afterFailedReservation = inventoryRepository.findById(inventory.getId()).orElseThrow();

        Assertions.assertEquals(1, afterFailedReservation.getAvailableQuantity());

        Inventory updatedInventory = inventoryRepository.findById(inventory.getId()).orElseThrow();
        Assertions.assertEquals(1, updatedInventory.getAvailableQuantity());
        Assertions.assertEquals(1, reservationRepository.countByInventory_Id(inventory.getId()));


    }


}
