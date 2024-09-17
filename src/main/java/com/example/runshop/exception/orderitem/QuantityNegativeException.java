package com.example.runshop.exception.orderitem;

public class QuantityNegativeException extends IllegalArgumentException {
    public QuantityNegativeException(String message) {
        super(message);
    }
}
