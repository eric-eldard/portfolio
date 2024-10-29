package com.eric_eldard.portfolio.service.auth;

import static com.eric_eldard.portfolio.util.Constants.$;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.eric_eldard.portfolio.model.auth.JwsAuthToken;
import com.eric_eldard.portfolio.util.ClaimConstants;

@Service
public class SecurityContextServiceImpl implements SecurityContextService
{
    @Nonnull
    @Override
    public String getCurrentUsersNameNonNull()
    {
        return getStringClaimOrElse(ClaimConstants.USERNAME, "anonymous");
    }

    @Nullable
    @Override
    public String getCurrentUsersNameNullable()
    {
        return getStringClaimOrElse(ClaimConstants.USERNAME, null);
    }

    @Nullable
    @Override
    public Long getCurrentUsersIdNullable()
    {
        // Principal subject is stored as a String, but we want a Long back
        return getClaimOrElse(ClaimConstants.USER_ID, () -> null, obj -> Long.parseLong(obj.toString()));
    }

    @Override
    public void setAuthentication(Authentication authentication)
    {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String getStringClaimOrElse(String claim, String backup)
    {
        return getClaimOrElse(claim, () -> backup, Object::toString);
    }

    private <R> R getClaimOrElse(String claim, Supplier<R> backupSupplier, Function<Object, R> converter)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null)
        {
            return backupSupplier.get();
        }

        if (auth instanceof JwsAuthToken jwsAuthToken)
        {
            Object claimValue = jwsAuthToken
                .getPrincipal()
                .getPayload()
                .get(claim);

            Objects.requireNonNull(claimValue, () -> $."Illegal null \{claim} in JwsAuthToken");
            return converter.apply(claimValue);
        }

        throw new IllegalStateException(
            $."Found unsupported Authentication type \{auth.getClass().getSimpleName()} in the SecurityContext");
    }
}