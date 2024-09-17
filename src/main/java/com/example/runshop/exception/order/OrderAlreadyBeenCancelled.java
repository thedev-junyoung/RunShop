package com.example.runshop.exception.order;

public class OrderAlreadyBeenCancelled extends RuntimeException {
    public OrderAlreadyBeenCancelled(String message) {
        super(message);
    }
}
