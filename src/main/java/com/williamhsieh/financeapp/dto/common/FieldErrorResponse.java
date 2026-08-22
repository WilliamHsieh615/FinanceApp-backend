package com.williamhsieh.financeapp.dto.common;

public record FieldErrorResponse(
    String field,
    String message
) {
}
