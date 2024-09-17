package com.example.runshop.exception.order;

public class OrderAlreadyBeenCancelledException extends RuntimeException {
    public OrderAlreadyBeenCancelledException(String message) {
        super(message);
    }
}
