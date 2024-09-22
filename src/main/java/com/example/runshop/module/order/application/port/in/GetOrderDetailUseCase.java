package com.example.runshop.module.order.application.port.in;

import com.example.runshop.model.dto.order.OrderDetailDTO;

public interface GetOrderDetailUseCase {
    OrderDetailDTO getOrderDetail(Long orderId);
}
