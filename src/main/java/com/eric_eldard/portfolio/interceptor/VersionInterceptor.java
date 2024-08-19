package com.eric_eldard.portfolio.interceptor;

import com.eric_eldard.portfolio.util.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class VersionInterceptor implements HandlerInterceptor
{
    private final String version;

    public VersionInterceptor(@Value("${portfolio.app.version:unset}") String version)
    {
        this.version = version;
    }

    @Override
    public void postHandle(@NotNull HttpServletRequest request,
                           @NotNull HttpServletResponse response,
                           @NotNull Object handler,
                           ModelAndView modelAndView)
    {
        if (!oldPortfolioRequest(request) && modelAndView != null)
        {
            modelAndView.getModelMap().addAttribute("portfolio_app_version", version);
        }
    }

    private boolean oldPortfolioRequest(HttpServletRequest request)
    {
        return request.getRequestURI().startsWith(Constants.OLD_PORTFOLIO_PATH);
    }
}