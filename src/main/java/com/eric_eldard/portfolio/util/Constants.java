package com.eric_eldard.portfolio.util;

public final class Constants
{
    public static final StringTemplate.Processor<String, RuntimeException> $ = STR;

    public static final String ASSETS_PATH = "/portfolio/assets";

    public static final String CSRF_HEADER_NAME = "X-Csrf-Token";

    public static final int FAILED_LOGINS_BEFORE_ACCOUNT_LOCK = 10;

    public static final String JWT_COOKIE_NAME = "authToken";

    public static final int MIN_PASSWORD_CHARS = 8;

    public static final String SERVER_START_CLAIM = "server_start";

    private Constants()
    {
        // constants ctor
    }
}