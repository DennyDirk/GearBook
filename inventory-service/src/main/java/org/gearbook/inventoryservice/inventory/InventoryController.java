package org.gearbook.inventoryservice.inventory;

import jakarta.validation.Valid;
import org.gearbook.inventoryservice.inventory.dto.InventoryRequest;
import org.gearbook.inventoryservice.inventory.dto.InventoryResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController
{
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService)
    {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public List<InventoryResponse> findAll()
    {
        return inventoryService.findAll();
    }

    @GetMapping("/{id}")
    public InventoryResponse getInventoryById(@PathVariable Long id)
    {
       return inventoryService.getInventoryById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryResponse createInventory(@Valid @RequestBody InventoryRequest inventoryRequest)
    {
        return inventoryService.createInventory(inventoryRequest);
    }
}
