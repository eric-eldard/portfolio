package com.eric_eldard.portfolio.controller.unauthenticated;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.Cookie;
import java.net.URI;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.eric_eldard.portfolio.model.auth.Credentials;
import com.eric_eldard.portfolio.model.user.LoginAttempt;
import com.eric_eldard.portfolio.model.user.PortfolioUser;
import com.eric_eldard.portfolio.model.user.PortfolioUserDto;
import com.eric_eldard.portfolio.model.user.enumeration.LoginFailureReason;
import com.eric_eldard.portfolio.model.user.enumeration.LoginOutcome;
import com.eric_eldard.portfolio.test.BaseMvcIntegrationTest;
import com.eric_eldard.portfolio.test.TestUtils;
import com.eric_eldard.portfolio.util.Constants;

public class AuthenticationControllerIT extends BaseMvcIntegrationTest
{
    @Test
    @SneakyThrows
    public void testSuccessfulLogin()
    {
        PortfolioUserDto dto = TestUtils.makePortfolioUserDto();
        PortfolioUser user = userService().create(dto);

        // Verify user is given an auth token and redirected into the portfolio
        login(dto)
            .andExpect(status().is3xxRedirection())
            .andDo(result ->
            {
                // Verify auth token attributes
                Cookie authToken = result.getResponse().getCookie(Constants.JWT_COOKIE_NAME);
                assertNotNull(authToken);
                assertTrue(authToken.isHttpOnly());
                assertTrue(authToken.getSecure());

                // Verify redirect is successful when auth token presented
                String redirectedUrl = result.getResponse().getRedirectedUrl();
                assertNotNull(redirectedUrl);
                mockMvc().perform(MockMvcRequestBuilders.get(redirectedUrl)
                    .cookie(authToken)
                ).andExpect(status().isOk());
            });

        user = refreshUser(user);

        assertEquals(LoginOutcome.SUCCESS, user.getLoginAttempts().getFirst().getOutcome());
    }

    @Test
    @SneakyThrows
    public void testFailedLogin()
    {
        PortfolioUserDto dto = TestUtils.makePortfolioUserDto();
        PortfolioUser user = userService().create(dto);

        loginWithBadPassword(dto)
            .andExpect(status().isUnauthorized());

        user = refreshUser(user);

        assertLatestLoginFailed(user, LoginFailureReason.BAD_CREDENTIALS);
        assertEquals(1, user.getFailedPasswordAttempts());
    }

    @Test
    @SneakyThrows
    public void testLockedAccountCannotLogin()
    {
        PortfolioUserDto dto = TestUtils.makePortfolioUserDto();
        PortfolioUser user = userService().create(dto);

        // Fail with wrong password until account is locked
        for (int i = 1; i <= Constants.FAILED_LOGINS_BEFORE_ACCOUNT_LOCK; i++)
        {
            loginWithBadPassword(dto)
                .andExpect(status().isUnauthorized());

            user = refreshUser(user);

            assertLatestLoginFailed(user, LoginFailureReason.BAD_CREDENTIALS);
            assertEquals(i, user.getFailedPasswordAttempts());
        }

        // Try correct password after account is locked
        login(dto)
            .andExpect(status().isForbidden());

        user = refreshUser(user);

        assertLatestLoginFailed(user, LoginFailureReason.ACCOUNT_LOCKED);

        // Verify login failure due to locked account does not increment failed password attempts
        assertEquals(Constants.FAILED_LOGINS_BEFORE_ACCOUNT_LOCK, user.getFailedPasswordAttempts());
    }

    @Test
    @SneakyThrows
    public void testExpiredAccountCannotLogin()
    {
        PortfolioUserDto dto = TestUtils.makePortfolioUserDto();
        PortfolioUser user = userService().create(dto);
        userService().setAuthorizedUntil(user.getId(), TestUtils.yesterday());

        login(dto)
            .andExpect(status().isForbidden());

        user = refreshUser(user);

        assertLatestLoginFailed(user, LoginFailureReason.ACCOUNT_EXPIRED);
    }

    @Test
    @SneakyThrows
    public void testDisabledAccountCannotLogin()
    {
        PortfolioUserDto dto = TestUtils.makePortfolioUserDto();
        PortfolioUser user = userService().create(dto);
        userService().setEnabled(user.getId(), false);

        login(dto)
            .andExpect(status().isForbidden());

        user = refreshUser(user);

        assertLatestLoginFailed(user, LoginFailureReason.ACCOUNT_DISABLED);
    }

    @Test
    @SneakyThrows
    public void testDeletedAccountCannotLogin()
    {
        PortfolioUserDto dto = TestUtils.makePortfolioUserDto();
        PortfolioUser user = userService().create(dto);
        userService().delete(user.getId());

        login(dto)
            .andExpect(status().isUnauthorized());
    }


    private URI makeLoginUri()
    {
        return makeBaseUri()
            .path("/login")
            .build()
            .toUri();
    }

    @SneakyThrows
    private ResultActions login(PortfolioUserDto dto)
    {
        Credentials credentials = new Credentials();
        credentials.setUsername(dto.getUsername());
        credentials.setPassword(dto.getPassword());

        return mockMvc().perform(MockMvcRequestBuilders.post(makeLoginUri())
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonify(credentials))
        );
    }

    private ResultActions loginWithBadPassword(PortfolioUserDto dto) throws Exception
    {
        Credentials credentials = new Credentials();
        credentials.setUsername(dto.getUsername());
        credentials.setPassword(dto.getPassword() + "-extra-garbage");

        return mockMvc().perform(MockMvcRequestBuilders.post(makeLoginUri())
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonify(credentials))
        );
    }

    private PortfolioUser refreshUser(PortfolioUser user)
    {
        return userService().findFullyHydratedById(user.getId()).orElseThrow();
    }

    private void assertLatestLoginFailed(PortfolioUser user, LoginFailureReason reason)
    {
        LoginAttempt loginAttempt = user.getLoginAttempts().getLast();
        assertEquals(LoginOutcome.FAILURE, loginAttempt.getOutcome());
        assertEquals(reason, loginAttempt.getFailureReason());
    }
}
