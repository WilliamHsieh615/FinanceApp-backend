package com.williamhsieh.financeapp.security;

import java.io.IOException;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.williamhsieh.financeapp.constant.auth.AuthEventCodes;
import com.williamhsieh.financeapp.dto.common.ApiErrorResponse;
import com.williamhsieh.financeapp.service.auth.AuthEventAuditService;
import com.williamhsieh.financeapp.service.auth.LoginRequestMetadata;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

@Component
public class RestAuthenticationEntryPoint
    implements AuthenticationEntryPoint {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            RestAuthenticationEntryPoint.class
        );

    private final ObjectMapper objectMapper;
    private final AuthEventAuditService authEventAuditService;

    public RestAuthenticationEntryPoint(
        ObjectMapper objectMapper,
        AuthEventAuditService authEventAuditService
    ) {
        this.objectMapper = objectMapper;
        this.authEventAuditService =
            authEventAuditService;
    }

    @Override
    public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authenticationException
    ) throws IOException {
        String code = "UNAUTHORIZED";
        String message = "需要有效的 Access Token";

        if (hasBearerToken(request)) {
            boolean expired =
                isExpiredToken(
                    authenticationException
                );

            code = expired
                ? AuthEventCodes.ACCESS_TOKEN_EXPIRED
                : AuthEventCodes.ACCESS_TOKEN_INVALID;

            message = expired
                ? "Access Token 已過期"
                : "Access Token 無效";

            recordAuthenticationFailure(
                request,
                code,
                message
            );
        }

        ApiErrorResponse errorResponse =
            ApiErrorResponse.of(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED
                    .getReasonPhrase(),
                code,
                message,
                request.getRequestURI()
            );

        response.setStatus(
            HttpStatus.UNAUTHORIZED.value()
        );

        response.setContentType(
            MediaType.APPLICATION_JSON_VALUE
        );

        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(
            response.getOutputStream(),
            errorResponse
        );
    }

    private boolean hasBearerToken(
        HttpServletRequest request
    ) {
        String authorization =
            request.getHeader(
                HttpHeaders.AUTHORIZATION
            );

        if (authorization == null) {
            return false;
        }

        if (!authorization.regionMatches(
            true,
            0,
            "Bearer ",
            0,
            7
        )) {
            return false;
        }

        return !authorization
            .substring(7)
            .isBlank();
    }

    private boolean isExpiredToken(
        Throwable throwable
    ) {
        Throwable current = throwable;

        while (current != null) {
            if (current
                instanceof JwtValidationException
                    jwtValidationException) {

                boolean expired =
                    jwtValidationException
                        .getErrors()
                        .stream()
                        .map(error ->
                            error.getDescription()
                        )
                        .filter(description ->
                            description != null
                        )
                        .map(description ->
                            description.toLowerCase(
                                Locale.ROOT
                            )
                        )
                        .anyMatch(description ->
                            description.contains("expired")
                        );

                if (expired) {
                    return true;
                }
            }

            current = current.getCause();
        }

        return false;
    }

    private void recordAuthenticationFailure(
        HttpServletRequest request,
        String eventCode,
        String failureDetail
    ) {
        try {
            LoginRequestMetadata metadata =
                LoginRequestMetadata.from(request);

            authEventAuditService.recordFailure(
                eventCode,
                null,
                null,
                failureDetail,
                null,
                null,
                metadata
            );
        } catch (Exception exception) {
            /*
             * Audit 寫入失敗不能覆蓋原本應回傳的 401。
             */
            LOGGER.error(
                "Failed to write access token audit event: code={}, path={}",
                eventCode,
                request.getRequestURI(),
                exception
            );
        }
    }
}