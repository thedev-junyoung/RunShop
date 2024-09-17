package com.example.runshop.controller;

import com.example.runshop.model.dto.order.OrderDetailDTO;
import com.example.runshop.model.dto.order.OrderListDTO;
import com.example.runshop.model.dto.response.SuccessResponse;
import com.example.runshop.model.enums.OrderStatus;
import com.example.runshop.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 주문 생성
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestParam Long userId, @RequestParam double totalPrice, HttpServletRequest httpRequest) {
        orderService.createOrder(userId, totalPrice);
        return SuccessResponse.ok("주문이 성공적으로 생성되었습니다.", httpRequest.getRequestURI());
    }

    // 주문 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable Long id, HttpServletRequest httpRequest) {
        var order = orderService.findOrderOrThrow(id);
        return SuccessResponse.ok("주문을 성공적으로 조회했습니다.", order, httpRequest.getRequestURI());
    }

    // 유저의 주문 목록 조회
    // 주문 목록 조회
    @GetMapping
    public ResponseEntity<?> getOrderList(@RequestParam Long userId, HttpServletRequest httpRequest) {
        List<OrderListDTO> orders = orderService.getOrderList(userId);
        return SuccessResponse.ok("주문 목록 조회 성공", orders, httpRequest.getRequestURI());
    }
    // 주문 상세 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderDetail(@PathVariable Long orderId, HttpServletRequest httpRequest) {
        OrderDetailDTO orderDetail = orderService.getOrderDetail(orderId);
        return SuccessResponse.ok("주문 상세 조회 성공", orderDetail, httpRequest.getRequestURI());
    }

    // 주문 상태 변경
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status, HttpServletRequest httpRequest) {
        orderService.changeOrderStatus(id, status);
        return SuccessResponse.ok("주문 상태가 성공적으로 변경되었습니다.", httpRequest.getRequestURI());
    }

    // 주문 취소
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id, HttpServletRequest httpRequest) {
        orderService.cancelOrder(id);
        return SuccessResponse.ok("주문이 성공적으로 취소되었습니다.", httpRequest.getRequestURI());
    }
}
