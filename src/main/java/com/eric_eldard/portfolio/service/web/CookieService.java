package com.eric_eldard.portfolio.service.web;

import jakarta.servlet.http.Cookie;

public interface CookieService
{
    Cookie makeCookie(String name, String content);

    Cookie makeExpiredCookie(String name);
}