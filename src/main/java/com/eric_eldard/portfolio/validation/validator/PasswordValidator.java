package com.eric_eldard.portfolio.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.eric_eldard.portfolio.util.Constants;
import com.eric_eldard.portfolio.validation.annotation.ValidPassword;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String>
{
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context)
    {
        return isValid(password);
    }

    public static boolean isValid(String password)
    {
        return password != null && password.trim().length() >= Constants.MIN_PASSWORD_CHARS;
    }
}