package com.williamhsieh.financeapp.exception;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.williamhsieh.financeapp.dto.common.ApiErrorResponse;
import com.williamhsieh.financeapp.dto.common.FieldErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            GlobalExceptionHandler.class
        );

    @ExceptionHandler(
        MethodArgumentNotValidException.class
    )
    public ResponseEntity<ApiErrorResponse>
        handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
        ) {

        List<FieldErrorResponse> fieldErrors =
            exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError ->
                    new FieldErrorResponse(
                        fieldError.getField(),
                        fieldError.getDefaultMessage() == null
                            ? "欄位值不正確"
                            : fieldError.getDefaultMessage()
                    )
                )
                .distinct()
                .toList();

        ApiErrorResponse response =
            ApiErrorResponse.withFieldErrors(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST
                    .getReasonPhrase(),
                "VALIDATION_FAILED",
                "請求資料驗證失敗",
                request.getRequestURI(),
                fieldErrors
            );

        return ResponseEntity
            .badRequest()
            .body(response);
    }

    @ExceptionHandler(
        HttpMessageNotReadableException.class
    )
    public ResponseEntity<ApiErrorResponse>
        handleMessageNotReadableException(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
        ) {

        ApiErrorResponse response =
            ApiErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST
                    .getReasonPhrase(),
                "MALFORMED_REQUEST_BODY",
                "Request Body 缺少或 JSON 格式不正確",
                request.getRequestURI()
            );

        return ResponseEntity
            .badRequest()
            .body(response);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse>
        handleResponseStatusException(
            ResponseStatusException exception,
            HttpServletRequest request
        ) {

        int status = exception
            .getStatusCode()
            .value();

        HttpStatus httpStatus =
            HttpStatus.resolve(status);

        String error = httpStatus == null
            ? "HTTP Error"
            : httpStatus.getReasonPhrase();

        String message =
            exception.getReason() == null
                || exception.getReason().isBlank()
                    ? error
                    : exception.getReason();

        ApiErrorResponse response =
            ApiErrorResponse.of(
                status,
                error,
                resolveErrorCode(status),
                message,
                request.getRequestURI()
            );

        return ResponseEntity
            .status(status)
            .body(response);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse>
        handleNoResourceFoundException(
            NoResourceFoundException exception,
            HttpServletRequest request
        ) {

        ApiErrorResponse response =
            ApiErrorResponse.of(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND
                    .getReasonPhrase(),
                "RESOURCE_NOT_FOUND",
                "找不到指定的 API 或資源",
                request.getRequestURI()
            );

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse>
        handleUnexpectedException(
            Exception exception,
            HttpServletRequest request
        ) {

        LOGGER.error(
            "Unhandled exception: method={}, path={}",
            request.getMethod(),
            request.getRequestURI(),
            exception
        );

        ApiErrorResponse response =
            ApiErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR
                    .getReasonPhrase(),
                "INTERNAL_ERROR",
                "系統發生未預期的錯誤",
                request.getRequestURI()
            );

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response);
    }

    private String resolveErrorCode(int status) {
        return switch (status) {
            case 400 -> "BAD_REQUEST";
            case 401 -> "UNAUTHORIZED";
            case 403 -> "ACCESS_DENIED";
            case 404 -> "RESOURCE_NOT_FOUND";
            case 409 -> "CONFLICT";
            default -> "REQUEST_FAILED";
        };
    }
}