package com.eric_eldard.portfolio.test;

import static org.junit.jupiter.api.Assertions.assertThrows;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.function.Executable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import com.eric_eldard.portfolio.model.user.PortfolioUserDto;
import com.eric_eldard.portfolio.util.Constants;

@Slf4j
public class TestUtils
{
    public static <T extends Throwable> T assertThrowsAndPrintMessage(Class<T> expectedType, Executable executable)
    {
        T throwable = assertThrows(expectedType, executable);
        log.error("{} thrown with message: {}", expectedType.getSimpleName(), throwable.getMessage());
        return throwable;
    }

    public static PortfolioUserDto makePortfolioUserDto()
    {
        Date nextYear = new Date(System.currentTimeMillis() + Duration.ofDays(365).toMillis());
        return PortfolioUserDto.builder()
            .username("portfolio-viewer")
            .password(makePassword())
            .enabled(true)
            .authorizedUntil(nextYear)
            .build();
    }

    public static String makePassword()
    {
        return "x".repeat(Constants.MIN_PASSWORD_CHARS);
    }

    public static String makeShortPassword()
    {
        return "x".repeat(Constants.MIN_PASSWORD_CHARS - 1);
    }

    public static Date twoDaysAgo()
    {
        return dateAtSystemTz(LocalDateTime.now().minusDays(2));
    }

    public static Date yesterday()
    {
        return dateAtSystemTz(LocalDateTime.now().minusDays(1));
    }

    public static Date tomorrow()
    {
        return dateAtSystemTz(LocalDateTime.now().plusDays(1));
    }

    private static Date dateAtSystemTz(LocalDateTime dateTime)
    {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}