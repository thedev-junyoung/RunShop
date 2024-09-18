package com.example.runshop.controller;

import com.example.runshop.model.dto.response.SuccessResponse;
import com.example.runshop.service.InventoryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // 재고 감소
    @PatchMapping("/decrease")
    public ResponseEntity<?> decreaseStock(@RequestParam Long productId, @RequestParam int quantity, HttpServletRequest httpRequest) {
        inventoryService.decreaseStock(productId, quantity);
        return SuccessResponse.ok("재고가 성공적으로 감소되었습니다.", httpRequest.getRequestURI());
    }

    // 재고 증가
    @PatchMapping("/increase")
    public ResponseEntity<?> increaseStock(@RequestParam Long productId, @RequestParam int quantity, HttpServletRequest httpRequest) {
        inventoryService.increaseStock(productId, quantity);
        return SuccessResponse.ok("재고가 성공적으로 증가되었습니다.", httpRequest.getRequestURI());
    }

    // 특정 상품의 재고 조회
    @GetMapping("/{productId}")
    public ResponseEntity<?> getStock(@PathVariable Long productId, HttpServletRequest httpRequest) {
        int stock = inventoryService.getStock(productId);
        return SuccessResponse.ok("재고 조회 성공", stock, httpRequest.getRequestURI());
    }
}
