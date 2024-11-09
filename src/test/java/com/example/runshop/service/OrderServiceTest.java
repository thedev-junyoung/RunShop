package com.example.runshop.service;

import com.example.runshop.model.dto.order.OrderListDTO;
import com.example.runshop.model.entity.Inventory;
import com.example.runshop.model.entity.Product;
import com.example.runshop.model.entity.User;
import com.example.runshop.model.enums.Category;
import com.example.runshop.model.enums.OrderStatus;
import com.example.runshop.model.vo.inventory.StockQuantity;
import com.example.runshop.model.vo.orderitem.OrderQuantity;
import com.example.runshop.model.vo.product.ProductDescription;
import com.example.runshop.model.vo.product.ProductName;
import com.example.runshop.model.vo.product.ProductPrice;
import com.example.runshop.module.order.application.service.OrderService;
import com.example.runshop.module.order.domain.Order;
import com.example.runshop.module.order.domain.OrderItem;
import com.example.runshop.module.order.application.port.out.OrderRepository;
import com.example.runshop.module.payment.adapters.in.event.PaymentRequestEvent;
import com.example.runshop.module.payment.domain.PaymentMethod;
import com.example.runshop.utils.mapper.OrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Mock
    private UserService userService;

    @Mock
    private OrderMapper orderMapper;


    @Mock
    private ApplicationEventPublisher eventPublisher;

    private User user;
    private Order order;
    private OrderItem orderItem1;
    private OrderItem orderItem2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Given: User 생성 및 ID 설정
        user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        user.setName("테스트 유저");

        // Product1 생성 및 Inventory 설정
        Product product1 = Product.builder()
                .name(new ProductName("Product1"))
                .description(new ProductDescription("Product1 Description"))
                .price(new ProductPrice(BigDecimal.valueOf(5000)))
                .category(Category.TOP)
                .brand("Brand1")
                .enabled(true)
                .build();
        ReflectionTestUtils.setField(product1, "id", 1L);
        Inventory inventory1 = new Inventory(new StockQuantity(10));
        inventory1.setProduct(product1); // Product와 Inventory 연결
        product1.setInventory(inventory1); // Product에 Inventory 설정

        // Product2 생성 및 Inventory 설정
        Product product2 = Product.builder()
                .name(new ProductName("Product2"))
                .description(new ProductDescription("Product2 Description"))
                .price(new ProductPrice(BigDecimal.valueOf(5000)))
                .category(Category.TOP)
                .brand("Brand2")
                .enabled(true)
                .build();
        ReflectionTestUtils.setField(product2, "id", 2L);
        Inventory inventory2 = new Inventory(new StockQuantity(5));
        inventory2.setProduct(product2);
        product2.setInventory(inventory2);

        // OrderItem 생성
        orderItem1 = OrderItem.create(product1, new OrderQuantity(2), PaymentMethod.CREDIT_CARD);
        orderItem2 = OrderItem.create(product2, new OrderQuantity(1), PaymentMethod.CREDIT_CARD);
        List<OrderItem> orderItems = List.of(orderItem1, orderItem2);

        // Order 생성 및 ID 설정
        order = Order.create(user, BigDecimal.valueOf(10000), orderItems);
        ReflectionTestUtils.setField(order, "id", 1L);
    }

    @Test
    @DisplayName("주문을 성공적으로 생성하고 결제 이벤트를 발행한다")
    public void createOrderAndTriggerPaymentEventSuccessfully() {
        when(userService.findUserOrThrow(anyLong(), anyString())).thenReturn(user);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            ReflectionTestUtils.setField(savedOrder, "id", 1L);  // ID 설정
            return savedOrder;
        });

        // When: 주문 생성
        orderService.createOrder(user.getId(), new BigDecimal("10000.0"), List.of(orderItem1, orderItem2), PaymentMethod.CREDIT_CARD);

        // Then: 주문이 정상적으로 생성되고 결제 이벤트가 발행되었는지 확인
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(eventPublisher, times(1)).publishEvent(any(PaymentRequestEvent.class));
    }

    @Test
    @DisplayName("주문 목록을 페이징 처리하여 조회한다")
    public void getPagedOrderListSuccessfully() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Order> orders = List.of(order);
        Page<Order> pagedOrders = new PageImpl<>(orders, pageable, orders.size());

        when(orderRepository.findByUserId(anyLong(), eq(pageable))).thenReturn(pagedOrders);
        when(orderMapper.toOrderListDTO(any(Order.class))).thenReturn(
                new OrderListDTO(order.getId(), order.getOrderDate(), order.getStatus(), order.getTotalPrice())
        );

        Page<OrderListDTO> orderList = orderService.getOrderList(user.getId(), pageable);

        assertNotNull(orderList);
        assertEquals(1, orderList.getContent().size());
        verify(orderRepository, times(1)).findByUserId(anyLong(), eq(pageable));
    }

    @Test
    @DisplayName("주문을 성공적으로 취소하고 재고를 복구한다")
    public void cancelOrderSuccessfully() {
        // Given: 주문 객체에 ID 설정
        ReflectionTestUtils.setField(order, "id", 1L);

        // 주문을 찾을 수 있도록 Mock 설정
        when(orderRepository.findById(anyLong())).thenReturn(java.util.Optional.of(order));

        // 실제 Inventory의 증가 여부를 확인하기 위해, 상품 재고를 미리 가져오기
        int initialStockProduct1 = orderItem1.getProduct().getInventory().getStockQuantity().value();
        int initialStockProduct2 = orderItem2.getProduct().getInventory().getStockQuantity().value();

        // When: 주문 취소
        orderService.cancelOrder(order.getId());

        // Then: 주문이 정상적으로 취소되었는지 확인
        assertEquals(OrderStatus.ORDER_CANCELLATION, order.getStatus());
        assertEquals(initialStockProduct1 + orderItem1.getQuantity().value(),
                orderItem1.getProduct().getInventory().getStockQuantity().value());
        assertEquals(initialStockProduct2 + orderItem2.getQuantity().value(),
                orderItem2.getProduct().getInventory().getStockQuantity().value());

        // OrderRepository 호출 확인
        verify(orderRepository, times(1)).findById(anyLong());
    }
}
