package com.eric_eldard.portfolio.service.user;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Utilities for retrieving info from the Spring Security Context
 */
public interface SecurityContextService
{
    @Nonnull
    String getCurrentUsersNameNonNull();

    @Nullable
    String getCurrentUsersNameNullable();
}