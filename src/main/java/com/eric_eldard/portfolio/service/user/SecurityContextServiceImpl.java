package com.eric_eldard.portfolio.service.user;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

@Service
public class SecurityContextServiceImpl implements SecurityContextService
{
    @Nonnull
    @Override
    public String getCurrentUsersNameNonNull()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth == null ? "anonymous" : auth.getName();
    }

    @Nullable
    @Override
    public String getCurrentUsersNameNullable()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth == null ? null : auth.getName();
    }

    @Override
    public void setAuthentication(Authentication authentication)
    {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}