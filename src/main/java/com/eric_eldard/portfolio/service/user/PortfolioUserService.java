package com.eric_eldard.portfolio.service.user;

import com.eric_eldard.portfolio.model.user.LoginFailureReason;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.eric_eldard.portfolio.model.user.PortfolioUser;
import com.eric_eldard.portfolio.model.user.PortfolioUserDto;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PortfolioUserService extends UserDetailsService
{
    List<PortfolioUser> findAll();

    Optional<PortfolioUser> findById(long id);

    PortfolioUser create(@Nonnull PortfolioUserDto dto);

    void delete(long id);

    void unlock(long id);

    void setPassword(long id, String password);

    void setAuthorizedUntil(long id, Date date);

    void setInfiniteAuthorization(long id);

    void toggleEnabled(long id);

    void toggleRole(long id);

    void recordSuccessfulLogin(@Nonnull String username);

    void recordFailedLogin(@Nonnull String username, @Nonnull LoginFailureReason failureReason);
}