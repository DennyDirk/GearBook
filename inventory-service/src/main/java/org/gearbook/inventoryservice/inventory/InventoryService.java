package org.gearbook.inventoryservice.inventory;

import org.gearbook.inventoryservice.inventory.dto.InventoryRequest;
import org.gearbook.inventoryservice.inventory.dto.InventoryResponse;
import org.gearbook.inventoryservice.inventory.exception.InventoryDuplicateSkuException;
import org.gearbook.inventoryservice.inventory.exception.InventoryInvalidQuantityException;
import org.gearbook.inventoryservice.inventory.exception.InventoryNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService
{
    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository)
    {
        this.inventoryRepository = inventoryRepository;
    }

    public List<InventoryResponse> findAll()
    {
        return inventoryRepository.findAll().stream().map(InventoryResponse::from).toList();
    }

    public InventoryResponse getInventoryById(Long id)
    {
        return inventoryRepository.findById(id).map(InventoryResponse::from)
                .orElseThrow(() -> new InventoryNotFoundException(id));
    }

    public InventoryResponse createInventory(InventoryRequest request)
    {
        if (request.availableQuantity() > request.totalQuantity())
        {
            throw new InventoryInvalidQuantityException(request.totalQuantity(), request.availableQuantity());
        }
        if (inventoryRepository.existsBySku(request.sku()))
        {
            throw new InventoryDuplicateSkuException(request.sku());
        }
        Inventory inventory = InventoryRequest.toInventory(request);
        Inventory savedInventory = inventoryRepository.save(inventory);
        return InventoryResponse.from(savedInventory);
    }

}
