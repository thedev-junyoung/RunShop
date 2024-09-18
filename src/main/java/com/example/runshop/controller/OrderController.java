package com.example.runshop.controller;

import com.example.runshop.model.dto.order.OrderDetailDTO;
import com.example.runshop.model.dto.order.OrderListDTO;
import com.example.runshop.model.dto.payment.OrderRequest;
import com.example.runshop.model.dto.response.SuccessResponse;
import com.example.runshop.model.enums.OrderStatus;
import com.example.runshop.service.OrderService;
import com.example.runshop.service.PaymentService;
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
    private final PaymentService paymentService;

    public OrderController(OrderService orderService, PaymentService paymentService) {
        this.orderService = orderService;
        this.paymentService = paymentService;
    }

    // 주문 생성
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest, HttpServletRequest httpRequest) {
        orderService.createOrder(orderRequest.getUserId(), orderRequest.getAmount().value(), orderRequest.getOrderItems());
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

    // 결제 처리
    @PostMapping("/{id}/payment")
    public ResponseEntity<?> processPayment(
            @PathVariable Long id,
            @RequestBody OrderRequest orderRequest,
            HttpServletRequest httpRequest) {

        paymentService.processPayment(id, orderRequest);
        return SuccessResponse.ok("결제가 성공적으로 처리되었습니다.", httpRequest.getRequestURI());
    }
}
