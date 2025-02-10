package kr.co.makao.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ApiException {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "BAD_REQUEST"),
    ID_NOT_FOUND(HttpStatus.BAD_REQUEST, "ID_NOT_FOUND"),
    PRODUCT_ALREADY_ACTIVATED(HttpStatus.BAD_REQUEST, "PRODUCT_ALREADY_ACTIVATED"),
    IMAGE_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE_SERVER_ERROR");

    private final HttpStatus status;
    private final String message;

    public ApiExceptionImpl toException() {
        return new ApiExceptionImpl(status, message);
    }

    public ApiExceptionImpl toException(String message) {
        return new ApiExceptionImpl(status, message);
    }
}