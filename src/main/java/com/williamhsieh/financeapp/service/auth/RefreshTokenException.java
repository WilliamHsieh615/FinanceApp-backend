package com.williamhsieh.financeapp.service.auth;

import org.springframework.http.HttpStatus;

public class RefreshTokenException extends RuntimeException {

    private final HttpStatus status;
    private final String eventCode;
    private final Long userId;
    private final Long refreshTokenId;
    private final String email;
    private final String sessionId;

    public RefreshTokenException(
        HttpStatus status,
        String eventCode,
        String message,
        Long userId,
        Long refreshTokenId,
        String email,
        String sessionId
    ) {
        super(message);

        this.status = status;
        this.eventCode = eventCode;
        this.userId = userId;
        this.refreshTokenId = refreshTokenId;
        this.email = email;
        this.sessionId = sessionId;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getEventCode() {
        return eventCode;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getRefreshTokenId() {
        return refreshTokenId;
    }

    public String getEmail() {
        return email;
    }

    public String getSessionId() {
        return sessionId;
    }
}
