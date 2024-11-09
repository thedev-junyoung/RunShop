package com.example.runshop.module.order.domain;

import com.example.runshop.model.entity.Inventory;
import com.example.runshop.model.entity.Product;
import com.example.runshop.model.vo.orderitem.OrderQuantity;
import com.example.runshop.model.vo.product.ProductName;
import com.example.runshop.module.payment.domain.PaymentMethod;
import com.example.runshop.module.payment.domain.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OrderItemTest {

    @Mock
    private Product product;
    @Mock
    private Inventory inventory;

    @InjectMocks
    private OrderItem orderItem;

    private OrderQuantity orderQuantity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Product의 Inventory를 설정
        when(product.getInventory()).thenReturn(inventory);
        when(product.getName()).thenReturn(new ProductName("Test Product"));

        // OrderItem 인스턴스 생성
        orderQuantity = new OrderQuantity(5);
        orderItem = OrderItem.create(product, orderQuantity, PaymentMethod.CREDIT_CARD);
    }

    @Test
    @DisplayName("OrderItem 생성 성공")
    void createOrderItem_Success() {
        assertEquals(PaymentStatus.PENDING, orderItem.getPaymentStatus());
        assertEquals(PaymentMethod.CREDIT_CARD, orderItem.getPaymentMethod());
        assertEquals(orderQuantity, orderItem.getQuantity());
        assertEquals("Test Product", orderItem.getProductName().value());
    }

    @Test
    @DisplayName("재고 감소 테스트")
    void decreaseStock_Success() {
        orderItem.decreaseStock();
        verify(inventory, times(1)).decreaseStock(orderQuantity.value());
    }

    @Test
    @DisplayName("재고 복구 테스트")
    void restoreStock_Success() {
        orderItem.restoreStock();
        verify(inventory, times(1)).increaseStock(orderQuantity.value());
    }
}
