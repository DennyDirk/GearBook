package org.gearbook.inventoryservice.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long>
{
    boolean existsBySku(String sku);
}
