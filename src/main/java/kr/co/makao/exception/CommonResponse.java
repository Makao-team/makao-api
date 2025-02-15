package kr.co.makao.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public record CommonResponse<T>(String status, String message, T data) {
    public static <T> ResponseEntity<CommonResponse<T>> success(T data) {
        return ResponseEntity.ok(new CommonResponse<>("success", "OK", data));
    }

    public static <T> ResponseEntity<CommonResponse<T>> error(HttpStatus httpStatus, String message) {
        return ResponseEntity.status(httpStatus).body(new CommonResponse<>("error", message, null));
    }
}