package com.example.runshop.exception.product;

public class PriceNegativeException extends IllegalArgumentException {
    public PriceNegativeException(String message) {
        super(message);
    }
}
