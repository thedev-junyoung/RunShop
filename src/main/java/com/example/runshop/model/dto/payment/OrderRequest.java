package com.example.runshop.model.dto.payment;

import com.example.runshop.model.enums.PaymentMethod;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class OrderRequest {
    private Long orderId;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
}
