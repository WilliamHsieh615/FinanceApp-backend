package com.williamhsieh.financeapp.constant.auth;

public final class AuthEventCodes {

    public static final String TOKEN_REFRESH_SUCCESS =
        "TOKEN_REFRESH_SUCCESS";

    public static final String REFRESH_TOKEN_EXPIRED =
        "REFRESH_TOKEN_EXPIRED";

    public static final String REFRESH_TOKEN_REVOKED =
        "REFRESH_TOKEN_REVOKED";

    public static final String REFRESH_TOKEN_REUSE_DETECTED =
        "REFRESH_TOKEN_REUSE_DETECTED";

    public static final String LOGOUT_SUCCESS =
        "LOGOUT_SUCCESS";

    public static final String LOGOUT_INVALID_TOKEN =
        "LOGOUT_INVALID_TOKEN";

    public static final String ACCESS_TOKEN_EXPIRED =
        "ACCESS_TOKEN_EXPIRED";

    public static final String ACCESS_TOKEN_INVALID =
        "ACCESS_TOKEN_INVALID";

    public static final String REFRESH_TOKEN_INVALID =
        "REFRESH_TOKEN_INVALID";

    private AuthEventCodes() {
    }
}
