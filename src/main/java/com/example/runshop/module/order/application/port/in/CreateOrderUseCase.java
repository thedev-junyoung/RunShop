package com.example.runshop.module.order.application.port.in;

import com.example.runshop.module.order.domain.OrderItem;
import com.example.runshop.module.payment.domain.PaymentMethod;
import java.math.BigDecimal;
import java.util.List;

public interface CreateOrderUseCase {
    void createOrder(Long userId, BigDecimal totalPrice, List<OrderItem> orderItems, PaymentMethod paymentMethod);
}
