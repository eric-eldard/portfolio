package com.eric_eldard.portfolio.service.web;

import jakarta.servlet.http.Cookie;

public interface CookieService
{
    Cookie makeSessionCookie(String name, String content);

    Cookie makePersistentCookie(String name, String content, int ttlSeconds);

    Cookie makeExpiredCookie(String name);
}