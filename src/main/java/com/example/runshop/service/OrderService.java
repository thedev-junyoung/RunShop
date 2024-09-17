package com.example.runshop.service;

import com.example.runshop.config.RoleCheck;
import com.example.runshop.exception.order.OrderAlreadyBeenCancelledException;
import com.example.runshop.exception.order.OrderNotFoundException;
import com.example.runshop.model.dto.order.OrderDetailDTO;
import com.example.runshop.model.dto.order.OrderListDTO;
import com.example.runshop.model.entity.Order;
import com.example.runshop.model.entity.User;
import com.example.runshop.model.enums.OrderStatus;
import com.example.runshop.repository.OrderRepository;
import com.example.runshop.utils.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@Service
@Slf4j
@Transactional(readOnly = true)
public class OrderService {

    private final UserService userService;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderService(UserService userService, OrderRepository orderRepository, OrderMapper orderMapper) {
        this.userService = userService;
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    // 주문 생성
    @RoleCheck("CUSTOMER")
    public void createOrder(Long userId, BigDecimal totalPrice) {
        User user = userService.findUserOrThrow(userId, "주문 생성");
        createNewOrder(user, totalPrice);
    }

    // 주문 목록 조회
    public List<OrderListDTO> getOrderList(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(orderMapper::toOrderListDTO)
                .collect(Collectors.toList());
    }

    // 주문 상세 조회
    public OrderDetailDTO getOrderDetail(Long orderId) {
        Order order = findOrderOrThrow(orderId);
        return orderMapper.toOrderDetailDTO(order);
    }

    // 주문 취소
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = findOrderOrThrow(orderId);
        validateOrderState(order);
        order.setStatus(OrderStatus.ORDER_CANCELLATION);
        log.info("주문이 성공적으로 취소되었습니다. 주문 ID: {}", orderId);
    }

    // 주문 상태 변경
    @Transactional
    public void changeOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = findOrderOrThrow(orderId);
        order.setStatus(newStatus);
        log.info("주문 상태가 성공적으로 변경되었습니다. 주문 ID: {}, 새로운 상태: {}", orderId, newStatus);
    }

    // 주문 생성 메서드
    private void createNewOrder(User user, BigDecimal totalPrice) {
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalPrice(totalPrice);
        order.setOrderDate(LocalDateTime.now());
        orderRepository.save(order);
    }

    // 주문 조회 메서드
    public Order findOrderOrThrow(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    // 주문 상태 검증 메서드
    private void validateOrderState(Order order) {
        if (order.getStatus() == OrderStatus.ORDER_CANCELLATION) {
            throw new OrderAlreadyBeenCancelledException("이미 취소된 주문입니다.");
        }
    }
}