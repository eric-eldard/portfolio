package com.eric_eldard.portfolio.controller.advice;

import com.eric_eldard.portfolio.service.user.SecurityContextService;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PageViewAspect
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PageViewAspect.class);

    private final SecurityContextService securityContextService;

    public PageViewAspect(SecurityContextService securityContextService)
    {
        this.securityContextService = securityContextService;
    }

    /**
     * Advice after GetMappings in MVC Controllers to log page views by user
     * @param page the jsp served to the user
     */
    @AfterReturning(pointcut = "within(@org.springframework.stereotype.Controller *) && " +
        "@annotation(org.springframework.web.bind.annotation.GetMapping)", returning = "page")
    public void logPageView(Object page)
    {
        LOGGER.info("[{}] viewed page [{}]", securityContextService.getCurrentUsersNameNonNull(), page);
    }
}