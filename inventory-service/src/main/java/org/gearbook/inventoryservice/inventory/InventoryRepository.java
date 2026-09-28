package org.gearbook.inventoryservice.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryRepository extends JpaRepository<Inventory, Long>
{
    boolean existsBySku(String sku);

    @Modifying
    @Query("UPDATE Inventory i set i.availableQuantity = i.availableQuantity - :quantity WHERE i.id = :inventoryId AND i.active = true AND i.availableQuantity >= :quantity  ")
    int reserve(@Param("inventoryId") Long inventoryId, @Param("quantity") Integer quantity);
}
