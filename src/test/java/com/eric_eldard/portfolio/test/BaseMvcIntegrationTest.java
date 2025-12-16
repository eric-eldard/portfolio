package com.eric_eldard.portfolio.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;

import jakarta.annotation.Nullable;
import jakarta.inject.Inject;
import jakarta.servlet.http.Cookie;
import java.net.URI;
import java.time.Duration;
import java.util.Arrays;
import java.util.Date;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import com.eric_eldard.portfolio.PortfolioApp;
import com.eric_eldard.portfolio.model.auth.JwsAuthToken;
import com.eric_eldard.portfolio.model.user.PortfolioUser;
import com.eric_eldard.portfolio.model.user.PortfolioUserDto;
import com.eric_eldard.portfolio.model.user.enumeration.PortfolioAuthority;
import com.eric_eldard.portfolio.persistence.user.PortfolioUserRepository;
import com.eric_eldard.portfolio.security.filter.JwsFilter;
import com.eric_eldard.portfolio.service.auth.AuthenticationService;
import com.eric_eldard.portfolio.service.auth.SecurityContextService;
import com.eric_eldard.portfolio.service.user.PortfolioUserService;
import com.eric_eldard.portfolio.util.Constants;

@SpringBootTest(
    classes = {
        TestConfig.class,
        PortfolioApp.class
    },
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BaseMvcIntegrationTest
{
    private static final String BASE_URL = "http://localhost";

    @LocalServerPort
    private int port;

    @Inject
    private MockMvc mockMvc;

    @Inject
    private AuthenticationService authService;

    @Inject
    private PortfolioUserService userService;

    @Inject
    private PortfolioUserRepository userRepo;


    /**
     * Much faster than {@link DirtiesContext}
     */
    @AfterEach
    protected void deleteAllUsers()
    {
        userRepo.deleteAll();
    }


    protected MockMvc mockMvc()
    {
        return mockMvc;
    }

    protected PortfolioUserService userService()
    {
        return userService;
    }

    /**
     * Makes a dto for a user we want to take action on
     */
    protected PortfolioUserDto makeNonAdminUserDto()
    {
        return TestUtils.makePortfolioUserDto();
    }

    /**
     * Creates an admin user for use as a caller, and returns an auth token cookie to present on their behalf
     */
    protected Cookie asAdmin()
    {
        return createUserAndIssueJwtCookie(PortfolioUserDto.builder()
            .username("portfolio-admin-caller")
            .admin(true)
        );
    }

    /**
     * Creates a non-admin user for use as a caller, and returns an auth token cookie to present on their behalf
     */
    protected Cookie asPortfolioViewer()
    {
        return createUserAndIssueJwtCookie(PortfolioUserDto.builder()
            .username("portfolio-viewer-caller")
        );
    }

    /**
     * Creates a non-admin with {@link PortfolioAuthority#OLD_PORTFOLIO} authority for use as a caller, and returns an
     * auth token cookie to present on their behalf
     */
    protected Cookie asOldPortfolioViewer()
    {
        return createUserAndIssueJwtCookie(PortfolioUserDto.builder()
            .username("old-portfolio-viewer-caller"),
            PortfolioAuthority.OLD_PORTFOLIO
        );
    }

    /**
     * NOOP - Does not create a user and returns a null auth token cookie
     */
    protected Cookie asUnauthenticated()
    {
        return null;
    }

    private Cookie createUserAndIssueJwtCookie(PortfolioUserDto.PortfolioUserDtoBuilder builder,
                                               PortfolioAuthority... authorities)
    {
        Date nextYear = new Date(System.currentTimeMillis() + Duration.ofDays(365).toMillis());
        PortfolioUser user = userService.create(builder
            .password(TestUtils.makePassword())
            .authorizedUntil(nextYear)
            .enabled(true)
            .build()
        );
        Arrays.stream(authorities).forEach(authority -> userService.toggleAuth(user.getId(), authority));
        return new Cookie(Constants.JWT_COOKIE_NAME, authService.issueToken(user));
    }

    protected PortfolioUser makeAndSaveNonAdminUser()
    {
        return userService.create(makeNonAdminUserDto());
    }

    @SneakyThrows
    protected String jsonify(Object obj)
    {
        return new ObjectMapper().writeValueAsString(obj);
    }

    /**
     * Perform a GET op on the URI, without a CSRF token, and return ResultActions for continued chaining.
     */
    @SneakyThrows
    protected ResultActions get(URI uri, @Nullable Cookie authCookie)
    {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(uri);
        if (authCookie != null)
        {
            request.cookie(authCookie);
        }
        return mockMvc.perform(request);
    }

    /**
     * Perform an application/json POST to the URI, with a CSRF token, and return ResultActions for continued chaining.
     */
    protected ResultActions post(URI uri, Object body, @Nullable Cookie authCookie)
    {
        return performHttpActionWithJwtAndCsrf(
            MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonify(body)),
            authCookie
        );
    }

    /**
     * Perform an application/json PATCH to the URI, with a CSRF token, and return ResultActions for continued chaining.
     */
    protected ResultActions patch(URI uri, Object body, @Nullable Cookie authCookie)
    {
        return performHttpActionWithJwtAndCsrf(
            MockMvcRequestBuilders.patch(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonify(body)),
            authCookie
        );
    }

    /**
     * Perform a DELETE op on the URI, with a CSRF token, and return ResultActions for continued chaining.
     */
    protected ResultActions delete(URI uri, @Nullable Cookie authCookie)
    {
        return performHttpActionWithJwtAndCsrf(MockMvcRequestBuilders.delete(uri), authCookie);
    }

    /// We must add real auth tokens to each request (vs annotating with {@link WithMockUser}); otherwise, the test
    /// invokes the {@link JwsFilter}, where we short-circuit and continue the filter chain when no token is present,
    /// expecting the call to fail authorization (when required) down the line. The test then puts its own
    /// {@link Authentication} into the security context (instead of our {@link JwsAuthToken}), and so authorization is
    /// not failed.
    ///
    /// This introduces two problems:
    /// 1. This circumvents all of our authentication logic in {@link AuthenticationService}, which we want to test
    /// 2. We blow up in {@link SecurityContextService} when we retrieve the principal and can't cast it to
    ///    {@link JwsAuthToken}
    @SneakyThrows
    private ResultActions performHttpActionWithJwtAndCsrf(MockHttpServletRequestBuilder request, Cookie authCookie)
    {
        if (authCookie != null)
        {
            request.cookie(authCookie);
        }
        request.with(csrf().asHeader());
        return mockMvc.perform(request);
    }

    protected UriComponentsBuilder makeBaseUri()
    {
        return UriComponentsBuilder
            .fromUriString(BASE_URL)
            .port(port);
    }

    protected void assertRedirectPath(MvcResult result, String path)
    {
        String redirectUrl = result.getResponse().getRedirectedUrl();
        assertNotNull(redirectUrl);

        String redirectPath = redirectUrl.replace(makeBaseUri().toUriString(), "");
        assertEquals(path, redirectPath);
    }

    protected void assertRedirectToLogin(MvcResult result)
    {
        assertRedirectPath(result, "/login");
    }
}