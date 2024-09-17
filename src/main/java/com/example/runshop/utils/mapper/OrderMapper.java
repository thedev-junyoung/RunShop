package com.example.runshop.utils.mapper;

import com.example.runshop.model.dto.OrderItemDTO;
import com.example.runshop.model.dto.order.OrderDetailDTO;
import com.example.runshop.model.dto.order.OrderListDTO;
import com.example.runshop.model.entity.Order;
import com.example.runshop.model.entity.OrderItem;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    // 주문 목록 DTO 변환
    default OrderListDTO toOrderListDTO(Order order) {
        return new OrderListDTO(
                order.getId(),
                order.getOrderDate(),
                order.getStatus(),
                order.getTotalPrice()
        );
    }

    // 주문 상세 DTO 변환
    default OrderDetailDTO toOrderDetailDTO(Order order) {
        List<OrderItemDTO> orderItems = order.getOrderItems().stream()
                .map(this::toOrderItemDTO)
                .collect(Collectors.toList());

        return new OrderDetailDTO(
                order.getId(),
                order.getOrderDate(),
                order.getStatus(),
                order.getTotalPrice(),
                orderItems
        );
    }

    // 주문 아이템 DTO 변환
    default OrderItemDTO toOrderItemDTO(OrderItem item) {
        return OrderItemDTO.builder()
                .id(item.getId())
                .product(item.getProduct())  // 필요한 필드만 추가
                .quantity(item.getQuantity())
                .price(item.getProduct().getPrice().getValue())
                .build();
    }
}
