package com.williamhsieh.financeapp.constant.auth;

public final class LoginStatusCodes {

    public static final String SUCCESS =
        "SUCCESS";

    public static final String INVALID_CREDENTIALS =
        "INVALID_CREDENTIALS";

    public static final String ACCOUNT_LOCKED =
        "ACCOUNT_LOCKED";

    public static final String ACCOUNT_DISABLED =
        "ACCOUNT_DISABLED";

    public static final String EMAIL_NOT_VERIFIED =
        "EMAIL_NOT_VERIFIED";

    public static final String SMS_NOT_VERIFIED =
        "SMS_NOT_VERIFIED";

    public static final String UNKNOWN_ERROR =
        "UNKNOWN_ERROR";

    private LoginStatusCodes() {
    }
}