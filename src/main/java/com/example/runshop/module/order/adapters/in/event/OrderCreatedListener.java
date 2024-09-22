package com.example.runshop.module.order.adapters.in.event;


import com.example.runshop.module.order.domain.Order;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedListener {

    @EventListener
    public void handleOrderCreatedEvent(Order order) {
        // Logic to handle an order created event
        System.out.println("Order created: " + order.getId());
    }
}