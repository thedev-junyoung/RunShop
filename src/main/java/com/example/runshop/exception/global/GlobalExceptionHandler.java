package com.example.runshop.exception.global;

import com.example.runshop.exception.Inventory.InventoryNotFoundException;
import com.example.runshop.exception.Inventory.OutOfStockException;
import com.example.runshop.exception.cart.CartItemAlreadyExistsException;
import com.example.runshop.exception.cart.CartItemNotFoundException;
import com.example.runshop.exception.order.OrderAlreadyBeenCancelledException;
import com.example.runshop.exception.order.OrderNotFoundException;
import com.example.runshop.exception.orderitem.QuantityNegativeException;
import com.example.runshop.exception.payment.InvalidPaymentAmountException;
import com.example.runshop.exception.product.CharactersArrangeDescriptionException;
import com.example.runshop.exception.product.CharactersArrangeException;
import com.example.runshop.exception.product.PriceNegativeException;
import com.example.runshop.exception.product.ProductNotFoundException;
import com.example.runshop.exception.user.*;
import com.example.runshop.model.dto.response.SuccessResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ====================== Payment 관련 예외 ======================
    @ExceptionHandler(InvalidPaymentAmountException.class)
    public ResponseEntity<SuccessResponse<Void>> handleInvalidPaymentAmountException(InvalidPaymentAmountException ex, HttpServletRequest request) {
        return SuccessResponse.error(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    // ====================== OrderItem 관련 예외 ======================

    @ExceptionHandler(QuantityNegativeException.class)
    public ResponseEntity<SuccessResponse<Void>> handleQuantityNegativeException(QuantityNegativeException ex, HttpServletRequest request) {
        return SuccessResponse.error(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    // ====================== Inventory 관련 예외 ======================

    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<SuccessResponse<Void>> handleOutOfStockException(OutOfStockException ex, HttpServletRequest request) {
        return SuccessResponse.error(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(InventoryNotFoundException.class)
    public ResponseEntity<SuccessResponse<Void>> handleInventoryNotFoundException(InventoryNotFoundException ex, HttpServletRequest request) {
        return SuccessResponse.error(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    // ====================== Product 관련 예외 ======================

    @ExceptionHandler(CharactersArrangeDescriptionException.class)
    public ResponseEntity<SuccessResponse<Void>> handleCharactersArrangeDescriptionException(CharactersArrangeDescriptionException ex, HttpServletRequest request) {
        return SuccessResponse.error(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(PriceNegativeException.class)
    public ResponseEntity<SuccessResponse<Void>> handlePriceNegativeException(PriceNegativeException ex, HttpServletRequest request) {
        return SuccessResponse.error(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(CharactersArrangeException.class)
    public ResponseEntity<SuccessResponse<Void>> handleCharactersArrangeException(CharactersArrangeException ex, HttpServletRequest request) {
        return SuccessResponse.error(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<SuccessResponse<Void>> handleProductNotFoundException(ProductNotFoundException ex, HttpServletRequest request) {
        return SuccessResponse.error(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    // ====================== CartItem 관련 예외 ======================

    @ExceptionHandler(CartItemNotFoundException.class)
    public ResponseEntity<SuccessResponse<Void>> handleCartItemNotFoundException(CartItemNotFoundException ex, HttpServletRequest request) {
        return SuccessResponse.error(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(CartItemAlreadyExistsException.class)
    public ResponseEntity<SuccessResponse<Void>> handleCartItemAlreadyExistsException(CartItemAlreadyExistsException ex, HttpServletRequest request) {
        return SuccessResponse.error(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
    }

    // ====================== Order 관련 예외 ======================

    @ExceptionHandler(OrderAlreadyBeenCancelledException.class)
    public ResponseEntity<SuccessResponse<Void>> handleOrderAlreadyBeenCancelled(OrderAlreadyBeenCancelledException ex, HttpServletRequest request) {
        return SuccessResponse.error(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<SuccessResponse<Void>> handleOrderNotFoundException(OrderNotFoundException ex, HttpServletRequest request) {
        return SuccessResponse.error(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    // ====================== User 관련 예외 ======================

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<SuccessResponse<Void>> handleInvalidEmailException(InvalidEmailException ex, HttpServletRequest request) {
        return SuccessResponse.error(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<SuccessResponse<Void>> handleUserAlreadyExistsException(UserAlreadyExistsException ex, HttpServletRequest request) {
        return SuccessResponse.error(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<SuccessResponse<Void>> handleUserNotFoundException(UserNotFoundException ex, HttpServletRequest request) {
        return SuccessResponse.error(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<SuccessResponse<Void>> handleIncorrectPasswordException(IncorrectPasswordException ex, HttpServletRequest request) {
        return SuccessResponse.error(HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<SuccessResponse<Void>> handleDuplicateEmailException(DuplicateEmailException ex, HttpServletRequest request) {
        return SuccessResponse.error(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
    }

    // ====================== 공통 예외 처리 ======================

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<SuccessResponse<Void>> handleSecurityException(SecurityException ex, HttpServletRequest request) {
        return SuccessResponse.error(HttpStatus.FORBIDDEN, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<SuccessResponse<Void>> handleValidationExceptions(ConstraintViolationException ex, HttpServletRequest request) {
        return SuccessResponse.error(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<SuccessResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        return SuccessResponse.error(HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<SuccessResponse<Void>> handleGeneralException(HttpServletRequest request) {
        return SuccessResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.", request.getRequestURI());
    }
}
