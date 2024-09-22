package com.example.runshop.module.order.adapters.in.rest;

import com.example.runshop.model.dto.order.OrderDetailDTO;
import com.example.runshop.model.dto.order.OrderListDTO;
import com.example.runshop.model.dto.payment.OrderRequest;
import com.example.runshop.model.dto.response.SuccessResponse;
import com.example.runshop.module.order.application.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest, HttpServletRequest httpRequest) {
        orderService.createOrder(orderRequest.getUserId(), orderRequest.getAmount().value(), orderRequest.getOrderItems(), orderRequest.getPaymentMethod());
        return SuccessResponse.ok("주문이 성공적으로 생성되었습니다.", httpRequest.getRequestURI());
    }

    // 주문 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable Long id, HttpServletRequest httpRequest) {
        var order = orderService.findOrderOrThrow(id);
        return SuccessResponse.ok("주문을 성공적으로 조회했습니다.", order, httpRequest.getRequestURI());
    }

    // 유저의 주문 목록 조회
    @GetMapping
    public ResponseEntity<?> getOrderList(@RequestParam Long userId, Pageable pageable, HttpServletRequest httpRequest) {
        Page<OrderListDTO> orders = orderService.getOrderList(userId, pageable);
        return SuccessResponse.ok("주문 목록 조회 성공", orders, httpRequest.getRequestURI());
    }

    // 주문 상세 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderDetail(@PathVariable Long orderId, HttpServletRequest httpRequest) {
        OrderDetailDTO orderDetail = orderService.getOrderDetail(orderId);
        return SuccessResponse.ok("주문 상세 조회 성공", orderDetail, httpRequest.getRequestURI());
    }
}
