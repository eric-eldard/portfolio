package com.eric_eldard.portfolio.service.user;

import static com.eric_eldard.portfolio.util.Constants.$;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Nonnull;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.eric_eldard.portfolio.model.user.LoginAttempt;
import com.eric_eldard.portfolio.model.user.PortfolioUser;
import com.eric_eldard.portfolio.model.user.PortfolioUserDto;
import com.eric_eldard.portfolio.model.user.enumeration.LoginFailureReason;
import com.eric_eldard.portfolio.model.user.enumeration.PortfolioAuthority;
import com.eric_eldard.portfolio.persistence.user.PortfolioUserRepository;
import com.eric_eldard.portfolio.security.annotation.ClearsCsrfToken;
import com.eric_eldard.portfolio.security.annotation.RequiresClaimsRefresh;
import com.eric_eldard.portfolio.service.auth.SecurityContextService;
import com.eric_eldard.portfolio.util.Constants;

@Service
@AllArgsConstructor
public class PortfolioUserServiceImpl implements PortfolioUserService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PortfolioUserServiceImpl.class);

    private final PasswordEncoder passwordEncoder;

    private final PortfolioUserRepository portfolioUserRepo;

    private final SecurityContextService securityContextService;

    @Override
    public List<PortfolioUser> findAllFullyHydrated()
    {
        return portfolioUserRepo.findAllFullyHydrated();
    }

    @Override
    public Optional<PortfolioUser> findById(long id)
    {
        return portfolioUserRepo.findById(id);
    }

    @Override
    public Optional<PortfolioUser> findWithAuthoritiesById(long id)
    {
        return portfolioUserRepo.findWithAuthoritiesById(id);
    }

    @Override
    public Optional<PortfolioUser> findFullyHydratedById(long id)
    {
        return portfolioUserRepo.findFullyHydratedById(id);
    }

    @Override
    public PortfolioUser create(@Nonnull PortfolioUserDto dto)
    {
        String username = dto.getUsername().trim();

        if (StringUtils.isBlank(username))
        {
            throw new IllegalArgumentException("PortfolioUserDto with blank username somehow got through validation");
        }
        validatePassword(dto.getPassword());

        if (portfolioUserRepo.existsByUsername(username))
        {
            throw new IllegalArgumentException($."A user with the username [\{username}] already exists");
        }

        String hashedPassword = passwordEncoder.encode(dto.getPassword().trim());
        PortfolioUser user = new PortfolioUser(
            username,
            hashedPassword,
            dto.getAuthorizedUntil(),
            dto.isEnabled(),
            dto.isAdmin()
        );

        user = portfolioUserRepo.save(user);
        LOGGER.info("User [{}] created by [{}]", user.getUsername(), getRequesterUsername());

        return user;
    }

    @Override
    @ClearsCsrfToken
    @RequiresClaimsRefresh
    public void delete(long id)
    {
        PortfolioUser user = findById(id)
            .orElseThrow(() ->
                new IllegalArgumentException($."Cannot delete user with id [\{id}]; user not found"));

        portfolioUserRepo.delete(user);
        LOGGER.info("User [{}] deleted by [{}]", user.getUsername(), getRequesterUsername());
    }

    @Override
    @RequiresClaimsRefresh
    public void unlock(long id)
    {
        PortfolioUser user = findById(id)
            .orElseThrow(() ->
                new IllegalArgumentException($."Cannot unlock user with id [\{id}]; user not found"));

        user.setLockedOn(null);
        user.setFailedPasswordAttempts(0);
        user = portfolioUserRepo.save(user);

        LOGGER.info("User [{}] unlocked by [{}]", user.getUsername(), getRequesterUsername());
    }

    @Override
    @ClearsCsrfToken
    @RequiresClaimsRefresh
    public void setPassword(long id, String password)
    {
        validatePassword(password);

        PortfolioUser user = findById(id)
            .orElseThrow(() ->
                new IllegalArgumentException($."Cannot set password for user with id [\{id}]; user not found"));

        user.setPassword(passwordEncoder.encode(password.trim()));
        user.setFailedPasswordAttempts(0);
        user = portfolioUserRepo.save(user);

        LOGGER.info("[{}] changed password for user [{}]",
            getRequesterUsername(),
            user.getUsername()
        );
    }

    @Override
    @RequiresClaimsRefresh
    public void setAuthorizedUntil(long id, Date date)
    {
        PortfolioUser user = findById(id)
            .orElseThrow(() ->
                new IllegalArgumentException($."Cannot set access date for user with id [\{id}]; user not found"));

        user.setAuthorizedUntil(date);
        user = portfolioUserRepo.save(user);

        LOGGER.info("User [{}] is authorized until {}; set by [{}]",
            user.getUsername(),
            user.getAuthorizedUntil(),
            getRequesterUsername()
        );
    }

    @Override
    @RequiresClaimsRefresh
    public void setInfiniteAuthorization(long id)
    {
        PortfolioUser user = findById(id)
            .orElseThrow(() ->
                new IllegalArgumentException($."Cannot set infinite access for user id [\{id}]; user not found"));

        user.setAuthorizedUntil(null);
        user = portfolioUserRepo.save(user);

        LOGGER.info("User [{}] is authorized forever; set by [{}]",
            user.getUsername(),
            getRequesterUsername()
        );
    }

    @Override
    @RequiresClaimsRefresh
    public void setEnabled(long id, boolean enabled)
    {
        PortfolioUser user = findById(id)
            .orElseThrow(() ->
                new IllegalArgumentException($."Cannot enable/disable user with id [\{id}]; user not found"));

        user.setEnabled(enabled);
        user = portfolioUserRepo.save(user);

        LOGGER.info("User [{}] {} by [{}]",
            user.getUsername(),
            user.isEnabled() ? "enabled" : "disabled",
            getRequesterUsername()
        );
    }

    @Override
    @ClearsCsrfToken
    @RequiresClaimsRefresh
    public void setIsAdmin(long id, boolean isAdmin)
    {
        PortfolioUser user = findById(id)
            .orElseThrow(() ->
                new IllegalArgumentException($."Cannot promote/demote user with id [\{id}]; user not found"));

        user.setAdmin(isAdmin);
        user = portfolioUserRepo.save(user);

        LOGGER.info("User [{}] {} by [{}]",
            user.getUsername(),
            user.isAdmin() ? "promoted to admin" : "demoted to standard user",
            getRequesterUsername()
        );
    }

    @Override
    @RequiresClaimsRefresh
    public void toggleAuth(long id, @Nonnull PortfolioAuthority authority)
    {
        PortfolioUser user = findWithAuthoritiesById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                $."Cannot grant/remove authority [\{authority}] for user with id [\{id}]; user not found"));

        if (user.hasAuthority(authority))
        {
            user.removeAuthority(authority);
        }
        else
        {
            user.addAuthority(authority);
        }

        user = portfolioUserRepo.save(user);
        boolean granted = user.hasAuthority(authority);

        LOGGER.info("[{}] {} authority [{}] {} user [{}]",
            getRequesterUsername(),
            granted ? "granted" : "removed",
            authority,
            granted ? "to" : "from",
            user.getUsername()
        );
    }

    @Override
    public UserDetails loadUserByUsername(@Nonnull String username) throws UsernameNotFoundException
    {
        PortfolioUser user = portfolioUserRepo.findWithAuthoritiesByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException($."User with username [\{username}] not found"));
        return user;
    }

    @Override
    public void recordSuccessfulLogin(@Nonnull String username)
    {
        PortfolioUser user = portfolioUserRepo.findFullyHydratedByUsername(username)
            .orElseThrow(() ->
                new IllegalStateException(
                    $."Cannot find user [\{username}] after they successfully logged in...highly unusual"));

        user.getLoginAttempts().add(LoginAttempt.makeSuccessfulAttempt(user));
        user.setFailedPasswordAttempts(0);
        portfolioUserRepo.save(user);
    }

    @Override
    public void recordFailedLogin(@Nonnull String username, @Nonnull LoginFailureReason failureReason)
    {
        Optional<PortfolioUser> optUser = portfolioUserRepo.findFullyHydratedByUsername(username);
        if (optUser.isEmpty())
        {
            LOGGER.info("Failed login for non-existent user [{}]", username);
            return;
        }

        PortfolioUser user = optUser.get();

        if (failureReason == LoginFailureReason.BAD_CREDENTIALS)
        {
            user.incrementFailedPasswordAttempts();
            int remainingAttempts = Constants.FAILED_LOGINS_BEFORE_ACCOUNT_LOCK - user.getFailedPasswordAttempts();
            if (remainingAttempts <= 0)
            {
                if (user.isAccountLocked())
                {
                    LOGGER.debug("Recording failed login attempt for [{}]; this account is already locked", username);
                }
                else
                {
                    user.setLockedOn(new Date());
                    LOGGER.info("Recording failed login attempt for [{}]; this account is now locked", username);
                }
            }
            else
            {
                LOGGER.info("Recording failed login attempt for [{}]; user has {} tries remaining before their " +
                    "account is locked", username, remainingAttempts);
            }
        }

        user.getLoginAttempts().add(LoginAttempt.makeFailedAttempt(user, failureReason));
        portfolioUserRepo.save(user);
    }

    private void validatePassword(String password)
    {
        if (password == null || password.trim().length() < Constants.MIN_PASSWORD_CHARS)
        {
            throw new IllegalArgumentException(
                $."Password must be at least \{Constants.MIN_PASSWORD_CHARS} characters");
        }
    }

    private String getRequesterUsername()
    {
        return securityContextService.getCurrentUsersNameNonNull();
    }
}