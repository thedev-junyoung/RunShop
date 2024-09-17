package com.example.runshop.model.enums;

public enum OrderStatus {
    PENDING, // 주문 대기
    PAYMENT_COMPLETE, // 결제 완료

    SHIPPING_IN_PROGRESS, // 배송 중
    DELIVERY_COMPLETE, // 배송 완료
    ORDER_CANCELLATION // 주문 취소
}
