package com.eric_eldard.portfolio.service.user;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface SecurityContextService
{
    @Nonnull
    String getCurrentUsersNameNonNull();

    @Nullable
    String getCurrentUsersNameNullable();
}