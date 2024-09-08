package com.eric_eldard.portfolio.service.user;

import jakarta.annotation.Nonnull;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.eric_eldard.portfolio.model.user.PortfolioUser;
import com.eric_eldard.portfolio.model.user.PortfolioUserDto;
import com.eric_eldard.portfolio.model.user.enumeration.LoginFailureReason;
import com.eric_eldard.portfolio.model.user.enumeration.PortfolioAuthority;

public interface PortfolioUserService extends UserDetailsService
{
    List<PortfolioUser> findAllFullyHydrated();

    Optional<PortfolioUser> findById(long id);

    Optional<PortfolioUser> findWithAuthoritiesById(long id);

    Optional<PortfolioUser> findFullyHydratedById(long id);

    PortfolioUser create(@Nonnull PortfolioUserDto dto);

    void delete(long id);

    void unlock(long id);

    void setPassword(long id, String password);

    void setAuthorizedUntil(long id, Date date);

    void setInfiniteAuthorization(long id);

    void setEnabled(long id, boolean enabled);

    void setIsAdmin(long id, boolean isAdmin);

    void toggleAuth(long id, @Nonnull PortfolioAuthority authority);

    void recordSuccessfulLogin(@Nonnull String username);

    void recordFailedLogin(@Nonnull String username, @Nonnull LoginFailureReason failureReason);
}