package com.example.runshop.model.dto.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class SuccessResponse<T> {
    private HttpStatus status;
    private String message;
    private T data;
    private String path;

    // 생성자
    public SuccessResponse(HttpStatus status, String message, T data, String path) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.path = path;
    }
    // 성공 응답 생성 메서드 (데이터 포함)
    public static <T> ResponseEntity<SuccessResponse<T>> ok(String message, T data) {
        return new ResponseEntity<>(new SuccessResponse<>(HttpStatus.OK, message, data, null), HttpStatus.OK);
    }

    // 성공 응답 생성 메서드 (데이터 미포함)
    public static <T> ResponseEntity<SuccessResponse<T>> ok(String message) {
        return new ResponseEntity<>(new SuccessResponse<>(HttpStatus.OK, message, null, null), HttpStatus.OK);
    }

    // 성공 응답 생성 메서드 (데이터 및 경로 포함)
    public static <T> ResponseEntity<SuccessResponse<T>> ok(String message, T data, String path) {
        return new ResponseEntity<>(new SuccessResponse<>(HttpStatus.OK, message, data, path), HttpStatus.OK);
    }

    // 오류 응답 생성 메서드
    public static ResponseEntity<SuccessResponse<Void>> error(HttpStatus status, String message, String path) {
        return new ResponseEntity<>(new SuccessResponse<>(status, message, null, path), status);
    }
}
