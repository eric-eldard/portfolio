package com.eric_eldard.portfolio.security.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * For reasons I cannot fathom, {@link CookieCsrfTokenRepository} will set the CSRF cookie on outgoing requests and
 * produce the correct CSRF token for each incoming request, but {@link CsrfFilter} doesn't look in the cookies for CSRF
 * tokens—it only looks in headers and params. These locations are incompatible with the idea of HttpOnly cookies.
 * <p/>
 * This filter should be run before the {@link CsrfFilter}, so that it can:
 * <ul>
 *     <li>Read the CSRF cookie value</li>
 *     <li>
 *         Pass a new {@link HttpServletRequestWrapperWithCsrfHeader} into the filter chain with the CSRF cookie value
 *         set as the CSRF header.
 *     </li>
 * </ul>
 */
public class MapCsrfCookieToHeaderFilter extends HttpFilter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MapCsrfCookieToHeaderFilter.class);

    private final String csrfCookieName;

    private final String csrfHeaderName;

    /**
     * {@link CookieCsrfTokenRepository#cookieName} and {@link CookieCsrfTokenRepository#headerName} are write-only
     * values, so instead of relying on that repository's default values, you must supply them explicitly and then
     * supply them here for matching during requests.
     */
    public MapCsrfCookieToHeaderFilter(String csrfCookieName, String csrfHeaderName)
    {
        this.csrfCookieName = csrfCookieName;
        this.csrfHeaderName = csrfHeaderName;
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws IOException, ServletException
    {
        Cookie[] cookies = request.getCookies();
        HttpServletRequestWrapperWithCsrfHeader requestWithCsrfHeader = null;

        if (cookies != null)
        {
            for (Cookie cookie : cookies)
            {
                if (cookie.getName().equals(csrfCookieName))
                {
                    requestWithCsrfHeader = new HttpServletRequestWrapperWithCsrfHeader(request, cookie.getValue());
                    break;
                }
            }
        }

        if (requestWithCsrfHeader == null)
        {
            LOGGER.debug("CSRF cookie not found");
            chain.doFilter(request, response);
        }
        else
        {
            LOGGER.debug("CSRF cookie found and set to header {}", csrfHeaderName);
            chain.doFilter(requestWithCsrfHeader, response);
        }
    }

    /**
     * {@link HttpServletRequest} headers are intended to be final once created; this wrapper allows the CSRF header
     * value to be recognized and to produce the CSRF token value, even when that's not part of the original request.
     */
    private class HttpServletRequestWrapperWithCsrfHeader extends HttpServletRequestWrapper
    {
        private final String csrfToken;

        public HttpServletRequestWrapperWithCsrfHeader(HttpServletRequest request, String csrfToken)
        {
            super(request);
            this.csrfToken = csrfToken;
        }

        @Override
        public String getHeader(String name)
        {
            if (csrfHeaderName.equals(name))
            {
                return csrfToken;
            }
            else
            {
                return super.getHeader(name);
            }
        }
    }
}