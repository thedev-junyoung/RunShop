package com.example.runshop.exception.global;

import com.example.runshop.exception.cart.CartItemAlreadyExistsException;
import com.example.runshop.exception.cart.CartItemNotFoundException;
import com.example.runshop.exception.user.IncorrectPasswordException;
import com.example.runshop.exception.user.UserAlreadyExistsException;
import com.example.runshop.exception.user.UserNotFoundException;
import com.example.runshop.model.dto.response.SuccessResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CartItemNotFoundException.class)
    public ResponseEntity<SuccessResponse<Void>> handleCartItemNotFoundException(CartItemNotFoundException ex, HttpServletRequest request) {
        return SuccessResponse.error(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(CartItemAlreadyExistsException.class)
    public ResponseEntity<SuccessResponse<Void>> handleCartItemAlreadyExistsException(CartItemAlreadyExistsException ex, HttpServletRequest request) {
        return SuccessResponse.error(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
    }
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<SuccessResponse<Void>> handleSecurityException(SecurityException ex, HttpServletRequest request) {
        return SuccessResponse.error(HttpStatus.FORBIDDEN, ex.getMessage(), request.getRequestURI());
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<SuccessResponse<Void>> handleValidationExceptions(ConstraintViolationException ex, HttpServletRequest request) {
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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<SuccessResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        return SuccessResponse.error(HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<SuccessResponse<Void>> handleGeneralException(Exception ex, HttpServletRequest request) {
        return SuccessResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.", request.getRequestURI());
    }
}
