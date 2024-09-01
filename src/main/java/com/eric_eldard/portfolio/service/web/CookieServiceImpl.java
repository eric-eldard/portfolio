package com.eric_eldard.portfolio.service.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;

import com.eric_eldard.portfolio.config.GlobalConfig;

@Service
public class CookieServiceImpl implements CookieService
{
    private final boolean secure;


    /**
     * @param secure defaults to {@code true} in application.properties; supports override for non-SSL test envs
     */
    public CookieServiceImpl(@Value("${portfolio.cookie.secure}") boolean secure)
    {
        this.secure = secure;
    }


    /**
     * Note: SameSite attribute controlled in {@link GlobalConfig}
     */
    @Override
    public Cookie makeSessionCookie(String name, String content)
    {
        Cookie cookie = new Cookie(name, content);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(secure);
        return cookie;
    }

    @Override
    public Cookie makePersistentCookie(String name, String content, int ttlSeconds)
    {
        Cookie cookie = makeSessionCookie(name, content);
        cookie.setMaxAge(ttlSeconds);
        return cookie;
    }

    @Override
    public Cookie makeExpiredCookie(String name)
    {
        return makePersistentCookie(name, "" , 0);
    }
}