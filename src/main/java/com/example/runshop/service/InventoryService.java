package com.example.runshop.service;

import com.example.runshop.exception.Inventory.InventoryNotFoundException;
import com.example.runshop.exception.Inventory.OutOfStockException;
import com.example.runshop.model.entity.Inventory;
import com.example.runshop.repository.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    // 재고 조회 메서드
    public int getStock(Long productId) {
        Inventory inventory = findByProductOrThrow(productId);
        return inventory.getStockQuantity().getValue();
    }

    // 재고 감소 메서드
    @Transactional
    public void reduceStock(Long productId, int quantity) {
        Inventory inventory = findByProductOrThrow(productId);
        if (inventory.getStockQuantity().getValue() < quantity) {
            throw new OutOfStockException("재고가 부족합니다.");
        }
        inventory.getStockQuantity().decreaseStock(quantity);
        inventoryRepository.save(inventory);
    }

    // 재고 증가 메서드
    @Transactional
    public void increaseStock(Long productId, int quantity) {
        Inventory inventory = findByProductOrThrow(productId);
        inventory.getStockQuantity().increaseStock(quantity);
        inventoryRepository.save(inventory);
    }

    // 재고 확인 메서드
    public Inventory findByProductOrThrow(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException("해당 상품에 대한 재고가 존재하지 않습니다."));
    }
}
