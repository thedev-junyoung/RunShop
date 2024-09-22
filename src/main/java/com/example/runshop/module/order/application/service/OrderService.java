package com.example.runshop.module.order.application.service;

import com.example.runshop.config.RoleCheck;
import com.example.runshop.exception.order.OrderAlreadyBeenCancelledException;
import com.example.runshop.exception.order.OrderNotFoundException;
import com.example.runshop.model.dto.order.OrderDetailDTO;
import com.example.runshop.model.dto.order.OrderListDTO;
import com.example.runshop.model.entity.Product;
import com.example.runshop.model.enums.OrderStatus;
import com.example.runshop.module.order.application.port.in.CancelOrderUseCase;
import com.example.runshop.module.order.application.port.in.CreateOrderUseCase;
import com.example.runshop.module.order.application.port.in.GetOrderDetailUseCase;
import com.example.runshop.module.order.domain.Order;
import com.example.runshop.module.order.domain.OrderItem;
import com.example.runshop.model.entity.User;
import com.example.runshop.module.order.application.port.out.OrderRepository;
import com.example.runshop.module.payment.adapters.in.event.PaymentFailedEvent;
import com.example.runshop.module.payment.adapters.in.event.PaymentRequestEvent;
import com.example.runshop.module.payment.adapters.in.event.PaymentSuccessEvent;
import com.example.runshop.module.payment.domain.PaymentMethod;
import com.example.runshop.service.InventoryService;
import com.example.runshop.service.UserService;
import com.example.runshop.utils.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
public class OrderService implements CreateOrderUseCase, CancelOrderUseCase, GetOrderDetailUseCase {

    private final UserService userService;
    private final OrderRepository orderRepository;  // Use the OrderRepository abstraction
    private final InventoryService inventoryService;
    private final ApplicationEventPublisher eventPublisher;
    private final OrderMapper orderMapper;

    public OrderService(UserService userService, OrderRepository orderRepository,
                        InventoryService inventoryService, ApplicationEventPublisher eventPublisher, OrderMapper orderMapper) {
        this.userService = userService;
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
        this.inventoryService = inventoryService;
        this.orderMapper = orderMapper;
    }



    // 주문 생성
    @RoleCheck("CUSTOMER")
    @Transactional
    @CacheEvict(value = "orderListCache", key = "#userId")
    public void createOrder(Long userId, BigDecimal totalPrice, List<OrderItem> orderItems, PaymentMethod paymentMethod) {
        User user = userService.findUserOrThrow(userId, "주문 생성");

        // 재고 감소
        for (OrderItem item : orderItems) {
            inventoryService.decreaseStock(item.getProduct().getId(), item.getQuantity().value());
        }

        // 주문 생성
        Order order = new Order(user, totalPrice, orderItems);
        orderRepository.save(order);

        // 결제 요청 이벤트 발행
        eventPublisher.publishEvent(new PaymentRequestEvent(this, order.getId(), totalPrice,paymentMethod));

        log.info("주문이 성공적으로 생성되었습니다. 주문 ID: {}", order.getId());
    }

    // 결제 성공 이벤트 리스너
    @EventListener
    @Transactional
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        Order order = findOrderOrThrow(event.getOrderId());
        order.completePayment();
        orderRepository.save(order);

        log.info("결제가 성공적으로 처리되었습니다. 주문 ID: {}", order.getId());
    }

    // 결제 실패 이벤트 리스너
    @EventListener
    @Transactional
    public void handlePaymentFailure(PaymentFailedEvent event) {
        Order order = findOrderOrThrow(event.getOrderId());
        order.cancelOrder();
        orderRepository.save(order);

        log.warn("결제가 실패하여 주문이 취소되었습니다. 주문 ID: {}", order.getId());
    }
    // 주문 목록 조회
    @Cacheable(value = "orderListCache", key = "#userId")
    public Page<OrderListDTO> getOrderList(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable).map(orderMapper::toOrderListDTO);

    }

    // 주문 상세 조회
    @Cacheable(value = "orderDetailCache", key = "#orderId")
    public OrderDetailDTO getOrderDetail(Long orderId) {
        Order order = findOrderOrThrow(orderId);
        return orderMapper.toOrderDetailDTO(order);
    }
    // 주문 조회
    public Order findOrderOrThrow(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

        // 주문 취소
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "orderDetailCache", key = "#orderId"),  // 주문 상세 캐시 무효화
            @CacheEvict(value = "orderListCache", key = "#order.user.id")  // 주문 목록 캐시 무효화
    })
    public void cancelOrder(Long orderId) {
        Order order = findOrderOrThrow(orderId);
        validateOrderState(order);

        // 각 주문 항목의 재고 복구
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            inventoryService.increaseStock(product.getId(), item.getQuantity().value());
        }

        // 주문 상태 변경
        order.setStatus(OrderStatus.ORDER_CANCELLATION);
        log.info("주문이 성공적으로 취소되었습니다. 주문 ID: {}", orderId);
    }
    // 주문 상태 검증 메서드
    private void validateOrderState(Order order) {
        if (order.getStatus() == OrderStatus.ORDER_CANCELLATION) {
            throw new OrderAlreadyBeenCancelledException("이미 취소된 주문입니다.");
        }
    }
}




//
//    // 주문 생성
//    @RoleCheck("CUSTOMER")
//    @Transactional
//    @CacheEvict(value = "orderListCache", key = "#userId") // 주문 목록 캐시 무효화
//    public void createOrder(Long userId, BigDecimal totalPrice, List<OrderItem> orderItems) {
//        User user = userService.findUserOrThrow(userId, "주문 생성");
//
//        // 각 주문 항목의 재고 감소
//        for (OrderItem item : orderItems) {
//            inventoryService.decreaseStock(item.getProduct().getId(), item.getQuantity().value());
//        }
//
//        // 주문 생성
//        createNewOrder(user, totalPrice, orderItems);
//    }
//
//    // 주문 목록 조회
//    @Cacheable(value = "orderListCache", key = "#userId")
//    public Page<OrderListDTO> getOrderList(Long userId, Pageable pageable) {
//        return orderRepository.findByUserId(userId, pageable).map(orderMapper::toOrderListDTO);
//
//    }
//
//    // 주문 상세 조회
//    @Cacheable(value = "orderDetailCache", key = "#orderId")
//    public OrderDetailDTO getOrderDetail(Long orderId) {
//        Order order = findOrderOrThrow(orderId);
//        return orderMapper.toOrderDetailDTO(order);
//    }
//
//    // 주문 취소
//    @Transactional
//    @Caching(evict = {
//            @CacheEvict(value = "orderDetailCache", key = "#orderId"),  // 주문 상세 캐시 무효화
//            @CacheEvict(value = "orderListCache", key = "#order.user.id")  // 주문 목록 캐시 무효화
//    })
//    public void cancelOrder(Long orderId) {
//        Order order = findOrderOrThrow(orderId);
//        validateOrderState(order);
//
//        // 각 주문 항목의 재고 복구
//        for (OrderItem item : order.getOrderItems()) {
//            Product product = item.getProduct();
//            inventoryService.increaseStock(product.getId(), item.getQuantity().value());
//        }
//
//        // 주문 상태 변경
//        order.setStatus(OrderStatus.ORDER_CANCELLATION);
//        log.info("주문이 성공적으로 취소되었습니다. 주문 ID: {}", orderId);
//    }
//
//    // 주문 상태 변경
//    @Transactional
//    public void changeOrderStatus(Long orderId, OrderStatus newStatus) {
//        Order order = findOrderOrThrow(orderId);
//        order.setStatus(newStatus);
//        log.info("주문 상태가 성공적으로 변경되었습니다. 주문 ID: {}, 새로운 상태: {}", orderId, newStatus);
//    }
//
//    // 주문 생성 메서드
//    private void createNewOrder(User user, BigDecimal totalPrice, List<OrderItem> orderItems) {
////        Order order = new Order();
////        order.setUser(user);
////        order.setStatus(OrderStatus.PENDING);
////        order.setTotalPrice(totalPrice);
////        order.setOrderDate(LocalDateTime.now());
////        order.setOrderItems(orderItems);  // 주문 항목 추가
////        orderRepository.save(order);
//    }
//
//    // 주문 조회 메서드
//    public Order findOrderOrThrow(Long orderId) {
//        return orderRepository.findById(orderId)
//                .orElseThrow(() -> new OrderNotFoundException(orderId));
//    }
//

//}
