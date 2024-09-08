package com.eric_eldard.portfolio.service.user;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import org.springframework.security.core.Authentication;

/**
 * Utilities for retrieving info from the Spring Security Context
 */
public interface SecurityContextService
{
    @Nonnull
    String getCurrentUsersNameNonNull();

    @Nullable
    String getCurrentUsersNameNullable();

    void setAuthentication(Authentication authentication);
}