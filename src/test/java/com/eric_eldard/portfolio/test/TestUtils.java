package com.eric_eldard.portfolio.test;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.function.Executable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import com.eric_eldard.portfolio.model.user.PortfolioUserDto;
import com.eric_eldard.portfolio.util.Constants;

public class TestUtils
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TestUtils.class);

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

    public static Date yesterday()
    {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        return Date.from(yesterday.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date tomorrow()
    {
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        return Date.from(tomorrow.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static <T extends Throwable> T assertThrowsAndPrintMessage(Class<T> expectedType, Executable executable)
    {
        T throwable = assertThrows(expectedType, executable);
        LOGGER.error("{} thrown with message: {}", expectedType.getSimpleName(), throwable.getMessage());
        return throwable;
    }
}