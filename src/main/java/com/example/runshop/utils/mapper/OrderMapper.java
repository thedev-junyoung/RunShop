package com.example.runshop.utils.mapper;

import com.example.runshop.model.dto.OrderItemDTO;
import com.example.runshop.model.dto.order.OrderDetailDTO;
import com.example.runshop.model.dto.order.OrderListDTO;
import com.example.runshop.module.order.domain.Order;
import com.example.runshop.module.order.domain.OrderItem;
import com.example.runshop.model.vo.orderitem.OrderQuantity;
import com.example.runshop.model.vo.product.ProductName;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

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

    // 주문 아이템 DTO 변환 (VO에서 값을 추출하여 매핑)
    @Mapping(target = "product", source = "productName", qualifiedByName = "productNameToString")
    @Mapping(target = "quantity", source = "quantity", qualifiedByName = "orderQuantityToInt")
    default OrderItemDTO toOrderItemDTO(OrderItem item) {
        return OrderItemDTO.builder()
                .id(item.getId())
                .product(item.getProduct())
                .quantity(item.getQuantity())          // OrderQuantity VO에서 값 추출
                .price(item.getProduct().getPrice().value())   // ProductPrice VO에서 값 추출
                .build();
    }

    // VO -> 기본 데이터형으로 변환하는 매핑 메서드
    @Named("productNameToString")
    default String productNameToString(ProductName productName) {
        return productName != null ? productName.value() : null;
    }

    @Named("orderQuantityToInt")
    default int orderQuantityToInt(OrderQuantity orderQuantity) {
        return orderQuantity != null ? orderQuantity.value() : 0;
    }
}
