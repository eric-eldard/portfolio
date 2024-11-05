package com.eric_eldard.portfolio.util;

import static com.eric_eldard.portfolio.util.Constants.$;

public final class PasswordUtils
{
    public static void validate(String password)
    {
        if (password == null || password.trim().length() < Constants.MIN_PASSWORD_CHARS)
        {
            throw new IllegalArgumentException(
                $."Password must be at least \{Constants.MIN_PASSWORD_CHARS} characters");
        }
    }

    private PasswordUtils()
    {
        // util ctor
    }
}