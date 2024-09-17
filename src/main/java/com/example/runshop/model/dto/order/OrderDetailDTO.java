package com.example.runshop.model.dto.order;

import com.example.runshop.model.dto.OrderItemDTO;
import com.example.runshop.model.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class OrderDetailDTO {
    private Long orderId;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private BigDecimal totalPrice;
    private List<OrderItemDTO> orderItems;

}
