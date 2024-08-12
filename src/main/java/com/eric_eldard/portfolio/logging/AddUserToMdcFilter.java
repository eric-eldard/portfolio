package com.eric_eldard.portfolio.logging;

import com.eric_eldard.portfolio.service.user.SecurityContextService;
import org.slf4j.MDC;
import org.springframework.security.web.context.SecurityContextHolderFilter;

import javax.servlet.FilterChain;
import javax.servlet.GenericFilter;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * Filter for adding the calling user's username to the MDC, exposing that info to the logging context. This filter
 * should always be placed immediately after the {@link SecurityContextHolderFilter}, ensuring we know who the user is
 * by the time this filter is invoked.
 */
public class AddUserToMdcFilter extends GenericFilter
{
    private final SecurityContextService securityContextService;

    public AddUserToMdcFilter(SecurityContextService securityContextService)
    {
        this.securityContextService = securityContextService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException
    {
        try
        {
            String username = securityContextService.getCurrentUsersNameNullable();
            if (username != null)
            {
                MDC.put("username", username);
            }
            chain.doFilter(request, response);
        }
        finally
        {
            MDC.remove("username");
        }
    }
}