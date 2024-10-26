package com.eric_eldard.portfolio.security.exception;

public class InvalidTokenException extends RuntimeException
{
    public InvalidTokenException(String message)
    {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause)
    {
        super(message, cause);
    }
}