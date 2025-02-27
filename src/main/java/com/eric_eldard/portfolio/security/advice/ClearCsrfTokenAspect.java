package com.eric_eldard.portfolio.security.advice;

import lombok.AllArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;

import org.springframework.stereotype.Component;

import com.eric_eldard.portfolio.security.annotation.ClearsCsrfToken;
import com.eric_eldard.portfolio.security.csrf.PortfolioCsrfTokenRepository;
import com.eric_eldard.portfolio.util.ReflectionUtils;

/**
 * Automatically wraps any method annotated with {@link ClearsCsrfToken} to cause its successful invocation to drop any
 * CSRF tokens stored for the user.
 */
@Aspect
@Component
@AllArgsConstructor
public class ClearCsrfTokenAspect
{
    private final PortfolioCsrfTokenRepository csrfTokenRepo;

    @AfterReturning("@annotation(com.eric_eldard.portfolio.security.annotation.ClearsCsrfToken)")
    public void after(JoinPoint joinPoint)
    {
        ClearsCsrfToken annotationInstance = ReflectionUtils.getAnnotation(joinPoint, ClearsCsrfToken.class);
        long userId = ReflectionUtils.getArgValue(joinPoint, annotationInstance.idParamName(), Long.class);
        csrfTokenRepo.invalidateForUser(userId);
    }
}