package com.eric_eldard.portfolio.test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import com.eric_eldard.portfolio.model.user.PortfolioUserDto;
import com.eric_eldard.portfolio.util.Constants;

public class TestUtils
{
    public static PortfolioUserDto makePortfolioUserDto()
    {
        Date nextYear = new Date(System.currentTimeMillis() + Duration.ofDays(365).toMillis());
        return PortfolioUserDto.builder()
            .username("portfolio-viewer")
            .password("x".repeat(Constants.MIN_PASSWORD_CHARS))
            .enabled(true)
            .authorizedUntil(nextYear)
            .build();
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
}