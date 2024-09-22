package com.example.runshop.module.order.application.port.out;

import com.example.runshop.module.order.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository {
    Order save(Order order);
    void delete(Order order);  // Changed from deleteById
    Page<Order> findByUserId(Long userId, Pageable pageable);
    Optional<Order> findById(Long orderId);
}

