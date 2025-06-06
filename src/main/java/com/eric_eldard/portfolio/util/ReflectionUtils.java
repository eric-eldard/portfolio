package com.eric_eldard.portfolio.util;

import lombok.SneakyThrows;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.springframework.core.annotation.AnnotationUtils;

public final class ReflectionUtils
{
    @SneakyThrows
    public static Object getField(Object obj, String fieldName)
    {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

    @SneakyThrows
    public static void setField(Object obj, String fieldName, Object value)
    {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    public static <A extends Annotation> A getAnnotation(JoinPoint joinPoint, Class<A> annotationType)
    {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        return AnnotationUtils.findAnnotation(method, annotationType);
    }

    public static <T> T getArgValue(JoinPoint joinPoint, String argumentName, Class<T> argumentType)
    {
        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
        int index = ArrayUtils.indexOf(codeSignature.getParameterNames(), argumentName);
        return argumentType.cast(joinPoint.getArgs()[index]);
    }

    private ReflectionUtils()
    {
        // util ctor
    }
}