package com.example.runshop.model.dto.payment;

import com.example.runshop.model.entity.OrderItem;
import com.example.runshop.model.enums.PaymentMethod;
import com.example.runshop.model.vo.payment.PaymentAmount;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRequest {
    private Long userId;
    private PaymentAmount amount;
    private List<OrderItem> orderItems;
    private PaymentMethod paymentMethod;

}
