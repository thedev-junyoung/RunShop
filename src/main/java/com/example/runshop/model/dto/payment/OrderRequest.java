package com.example.runshop.model.dto.payment;

import com.example.runshop.module.order.domain.OrderItem;
import com.example.runshop.model.vo.payment.PaymentAmount;
import com.example.runshop.module.payment.domain.PaymentMethod;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private Long userId;
    private PaymentAmount amount;
    private List<OrderItem> orderItems;
    private PaymentMethod paymentMethod;

}
