package com.eric_eldard.portfolio.security.advice;

import lombok.AllArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;

import org.springframework.stereotype.Component;

import com.eric_eldard.portfolio.security.annotation.RequiresClaimsRefresh;
import com.eric_eldard.portfolio.service.auth.AuthenticationService;
import com.eric_eldard.portfolio.util.ReflectionUtils;

/**
 * Automatically wraps any method annotated with {@link RequiresClaimsRefresh} to flag the user the method was called
 * for (if the method completes successfully). Flagged users will have their claims automatically refreshed upon their
 * next request.
 */
@Aspect
@Component
@AllArgsConstructor
public class TokenRefreshAspect
{
    private final AuthenticationService authenticationService;

    @AfterReturning("@annotation(com.eric_eldard.portfolio.security.annotation.RequiresClaimsRefresh)")
    public void after(JoinPoint joinPoint)
    {
        RequiresClaimsRefresh annotationInstance =
            ReflectionUtils.getAnnotationFromJoinPoint(joinPoint, RequiresClaimsRefresh.class);

        long userId =
            ReflectionUtils.getArgValueFromJoinPoint(joinPoint, annotationInstance.idParamName(), Long.class);

        authenticationService.requireFreshClaimsForUser(userId);
    }
}