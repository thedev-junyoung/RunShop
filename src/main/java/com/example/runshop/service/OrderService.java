package com.example.runshop.service;

import com.example.runshop.config.RoleCheck;
import com.example.runshop.exception.user.UserNotFoundException;
import com.example.runshop.model.entity.Order;
import com.example.runshop.model.entity.User;
import com.example.runshop.model.enums.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@Transactional(readOnly = true)
public class OrderService {

    private final UserService userService;
    private final OrderRepository orderRepository;

    public OrderService(UserService userService, OrderRepository orderRepository) {
        this.userService = userService;
        this.orderRepository = orderRepository;
    }

    // 주문 생성
    @RoleCheck("CUSTOMER")
    public Order createOrder(Long userId, double totalPrice) {
        // 사용자 조회
        User user = userService.findById(userId);
        if (user == null) {
            log.error("사용자를 찾을 수 없습니다. 사용자 ID: {}", userId);
            throw new UserNotFoundException("구매를 요청한 사용자를 찾을 수 없습니다.");
        }

        // 주문 생성 로직을 별도 메서드로 분리
        Order order = createNewOrder(user, totalPrice);

        // 주문 저장
        Order savedOrder = orderRepository.save(order);
        log.info("주문이 성공적으로 생성되었습니다. 주문 ID: {}", savedOrder.getId());

        return savedOrder;
    }


    // 주문 생성 메서드
    private Order createNewOrder(User user, double totalPrice) {
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING); // 기본적으로 PENDING 상태로 설정
        order.setTotalPrice(totalPrice);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    // 주문 취소
    public void cancelOrder() {
        log.info("주문 취소");
    }

    // 주문 상태 변경
    public void changeOrderStatus() {
        log.info("주문 상태 변경");
    }
}
