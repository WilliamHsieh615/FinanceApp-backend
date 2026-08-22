package com.williamhsieh.financeapp.service.auth;

import jakarta.servlet.http.HttpServletRequest;

public record LoginRequestMetadata(
    String ipAddress,
    String userAgent,
    String deviceName,
    String osName,
    String browserName
) {

    public static LoginRequestMetadata from(
        HttpServletRequest request
    ) {
        String userAgent = limitLength(
            request.getHeader("User-Agent"),
            500
        );

        return new LoginRequestMetadata(
            limitLength(request.getRemoteAddr(), 45),
            userAgent,
            null,
            null,
            null
        );
    }

    private static String limitLength(
        String value,
        int maxLength
    ) {
        if (value == null) {
            return null;
        }

        return value.length() <= maxLength
            ? value
            : value.substring(0, maxLength);
    }
}