package kr.co.makao.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiExceptionImpl extends RuntimeException {
    private final HttpStatus status;

    ApiExceptionImpl(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}