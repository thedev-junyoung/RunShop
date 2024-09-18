package com.example.runshop.exception.payment;

public class InvalidPaymentAmountException extends IllegalArgumentException {
    public InvalidPaymentAmountException(String message) {
        super(message);
    }
}
