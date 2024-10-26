package com.eric_eldard.portfolio.service.user;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.eric_eldard.portfolio.model.auth.JwsAuthToken;

@Service
public class SecurityContextServiceImpl implements SecurityContextService
{
    @Nonnull
    @Override
    public String getCurrentUsersNameNonNull()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null)
        {
            return ((JwsAuthToken) auth).username();
        }
        return "anonymous";
    }

    @Nullable
    @Override
    public String getCurrentUsersNameNullable()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null)
        {
            return ((JwsAuthToken) auth).username();
        }
        return null;
    }

    @Nullable
    @Override
    public Long getCurrentUsersIdNullable()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null)
        {
            return ((JwsAuthToken) auth).userId();
        }
        return null;
    }

    @Override
    public void setAuthentication(Authentication authentication)
    {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}