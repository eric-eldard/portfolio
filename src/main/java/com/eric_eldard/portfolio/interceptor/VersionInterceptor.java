package com.eric_eldard.portfolio.interceptor;

import lombok.AllArgsConstructor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.eric_eldard.portfolio.model.AdditionalLocation;
import com.eric_eldard.portfolio.properties.AdditionalLocations;

@Component
@AllArgsConstructor
public class VersionInterceptor implements HandlerInterceptor
{
    private final AdditionalLocations additionalLocations;

    @Value("${portfolio.app.version:unset}")
    private final String version;

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView)
    {
        if (!isAdditionalLocationRequest(request) &&
            isNotRedirect(response) &&
            modelAndView != null
        )
        {
            modelAndView.getModelMap().addAttribute("portfolio_app_version", version);
        }
    }

    /**
     * Additional locations are not a part of the app and so don't receive model attributes
     */
    private boolean isAdditionalLocationRequest(HttpServletRequest request)
    {
        return additionalLocations.getLocations()
            .stream()
            .map(AdditionalLocation::webPath)
            .anyMatch(webPath -> request.getRequestURI().startsWith(webPath));
    }

    private boolean isNotRedirect(HttpServletResponse response)
    {
        return !HttpStatusCode.valueOf(response.getStatus()).is3xxRedirection();
    }
}