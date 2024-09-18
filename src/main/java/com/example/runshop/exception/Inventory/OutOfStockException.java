package com.example.runshop.exception.Inventory;

public class OutOfStockException extends IllegalArgumentException {
    public OutOfStockException(String message) {
        super(message);
    }
}
