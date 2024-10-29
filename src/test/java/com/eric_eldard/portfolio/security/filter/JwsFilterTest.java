package com.eric_eldard.portfolio.security.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.cache.Cache;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;

import com.eric_eldard.portfolio.PortfolioApp;
import com.eric_eldard.portfolio.model.user.PortfolioUser;
import com.eric_eldard.portfolio.persistence.user.PortfolioUserRepository;
import com.eric_eldard.portfolio.security.util.JwtUtil;
import com.eric_eldard.portfolio.service.auth.AuthenticationService;
import com.eric_eldard.portfolio.service.user.PortfolioUserService;
import com.eric_eldard.portfolio.test.TestConfig;
import com.eric_eldard.portfolio.test.TestUtils;
import com.eric_eldard.portfolio.util.ClaimConstants;
import com.eric_eldard.portfolio.util.Constants;
import com.eric_eldard.portfolio.util.ReflectionUtils;

@SpringBootTest(
    classes = {
        TestConfig.class,
        PortfolioApp.class
    }
)
@ActiveProfiles("test")
public class JwsFilterTest
{
    @Inject
    private AuthenticationService authService;

    @Inject
    private JwtUtil jwtUtil;

    @Inject
    private PortfolioUserService userService;

    @Inject
    private PortfolioUserRepository userRepo;

    private JwsFilter jwsFilter;


    @BeforeEach
    public void reset()
    {
        jwsFilter = new JwsFilter(authService);
        ((Cache<Long, Date>) ReflectionUtils.getField(authService, "usersRequiringFreshClaims")).invalidateAll();
        userRepo.deleteAll();
    }


    @Test
    @SneakyThrows
    @Transactional
    public void testLockedAccountIsForbidden()
    {
        PortfolioUser user = userService.create(TestUtils.makePortfolioUserDto());
        user.setLockedOn(new Date());
        userRepo.save(user);

        MockHttpServletResponse response = makeRequest(authService.issueToken(user));

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    @SneakyThrows
    public void testExpiredAccountIsForbidden()
    {
        PortfolioUser user = userService.create(TestUtils.makePortfolioUserDto());
        userService.setAuthorizedUntil(user.getId(), TestUtils.yesterday());

        MockHttpServletResponse response = makeRequest(authService.issueToken(user));

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    @SneakyThrows
    public void testDisabledAccountIsForbidden()
    {
        PortfolioUser user = userService.create(TestUtils.makePortfolioUserDto());
        userService.setEnabled(user.getId(), false);

        MockHttpServletResponse response = makeRequest(authService.issueToken(user));

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    @SneakyThrows
    public void testDeletedAccountIsUnauthorized()
    {
        PortfolioUser user = userService.create(TestUtils.makePortfolioUserDto());
        userService.delete(user.getId());

        MockHttpServletResponse response = makeRequest(authService.issueToken(user));

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    @Test
    @SneakyThrows
    public void testDamagedAuthTokenIsUnauthorized()
    {
        PortfolioUser user = userService.create(TestUtils.makePortfolioUserDto());
        userService.delete(user.getId());

        String damagedClaims = authService.issueToken(user).substring(0, authService.issueToken(user).length() / 2);

        MockHttpServletResponse response = makeRequest(damagedClaims);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    @Test
    @SneakyThrows
    public void testTokenWithoutUserIdIsUnauthorized()
    {
        PortfolioUser user = userService.create(TestUtils.makePortfolioUserDto());

        Jws<Claims> claims = jwtUtil.resolveClaims(authService.issueToken(user));
        Map<String, Object> claimsMap = new HashMap<>(claims.getPayload());
        claimsMap.remove(ClaimConstants.USER_ID);

        String claimsMissingSubject = jwtUtil.buildToken(
            null,
            claimsMap,
            claims.getPayload().getIssuedAt(),
            claims.getPayload().getExpiration()
        );

        MockHttpServletResponse response = makeRequest(claimsMissingSubject);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    @Test
    @SneakyThrows
    public void testExpiredTokenIsUnauthorized()
    {
        PortfolioUser user = userService.create(TestUtils.makePortfolioUserDto());

        Jws<Claims> claims = jwtUtil.resolveClaims(authService.issueToken(user));
        String expiredClaims = jwtUtil.buildToken(
            claims.getPayload().getSubject(),
            claims.getPayload(),
            claims.getPayload().getIssuedAt(),
            TestUtils.yesterday()
        );

        MockHttpServletResponse response = makeRequest(expiredClaims);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    @Test
    @SneakyThrows
    public void testTokenWithoutIssuedAtIsUnauthorized()
    {
        PortfolioUser user = userService.create(TestUtils.makePortfolioUserDto());

        Jws<Claims> claims = jwtUtil.resolveClaims(authService.issueToken(user));
        String claimsMissingIssuedAt = jwtUtil.buildToken(
            claims.getPayload().getSubject(),
            claims.getPayload(),
            null,
            claims.getPayload().getExpiration()
        );

        MockHttpServletResponse response = makeRequest(claimsMissingIssuedAt);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    @Test
    @SneakyThrows
    public void testTokenFromFutureIsUnauthorized()
    {
        PortfolioUser user = userService.create(TestUtils.makePortfolioUserDto());

        Jws<Claims> claims = jwtUtil.resolveClaims(authService.issueToken(user));
        String claimsFromTheFuture = jwtUtil.buildToken(
            claims.getPayload().getSubject(),
            claims.getPayload(),
            TestUtils.tomorrow(),
            claims.getPayload().getExpiration()
        );

        MockHttpServletResponse response = makeRequest(claimsFromTheFuture);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    @Test
    @SneakyThrows
    public void testTokenWithoutServerStartIsUnauthorized()
    {
        PortfolioUser user = userService.create(TestUtils.makePortfolioUserDto());

        Jws<Claims> claims = jwtUtil.resolveClaims(authService.issueToken(user));
        Map<String, Object> claimsMap = new HashMap<>(claims.getPayload());
        claimsMap.remove(ClaimConstants.SERVER_START);

        String claimsMissingServerStart = jwtUtil.buildToken(
            claims.getPayload().getSubject(),
            claimsMap,
            claims.getPayload().getIssuedAt(),
            claims.getPayload().getExpiration()
        );

        MockHttpServletResponse response = makeRequest(claimsMissingServerStart);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    @Test
    @SneakyThrows
    public void testTokenWithoutUsernameIsUnauthorized()
    {
        PortfolioUser user = userService.create(TestUtils.makePortfolioUserDto());

        Jws<Claims> claims = jwtUtil.resolveClaims(authService.issueToken(user));
        Map<String, Object> claimsMap = new HashMap<>(claims.getPayload());
        claimsMap.remove(ClaimConstants.USERNAME);

        String claimsMissingUsername = jwtUtil.buildToken(
            claims.getPayload().getSubject(),
            claimsMap,
            claims.getPayload().getIssuedAt(),
            claims.getPayload().getExpiration()
        );

        MockHttpServletResponse response = makeRequest(claimsMissingUsername);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    @Test
    @SneakyThrows
    public void tokenRefreshedWhenUserRequiresFreshClaims()
    {
        PortfolioUser user = userService.create(TestUtils.makePortfolioUserDto());
        authService.requireFreshClaimsForUser(user.getId());

        Jws<Claims> claims = jwtUtil.resolveClaims(authService.issueToken(user));
        String claimsFromYesterday = jwtUtil.buildToken(
            claims.getPayload().getSubject(),
            claims.getPayload(),
            TestUtils.yesterday(),
            claims.getPayload().getExpiration()
        );

        MockHttpServletResponse response = makeRequest(claimsFromYesterday);

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        Claims originalClaims = jwtUtil.resolveClaims(claimsFromYesterday).getPayload();
        Claims newClaims = jwtUtil.resolveClaims(response.getCookie(Constants.JWT_COOKIE_NAME).getValue()).getPayload();

        assertTrue(newClaims.getIssuedAt().after(originalClaims.getIssuedAt()));
        assertEquals(originalClaims.getExpiration(), newClaims.getExpiration());
    }

    @Test
    @SneakyThrows
    public void tokenRefreshedAfterSystemRestart()
    {
        PortfolioUser user = userService.create(TestUtils.makePortfolioUserDto());

        Jws<Claims> jwsClaims = jwtUtil.resolveClaims(authService.issueToken(user));
        Map<String, Object> claimsFromYesterday = new HashMap<>(jwsClaims.getPayload());
        claimsFromYesterday.put(ClaimConstants.SERVER_START, TestUtils.yesterday());

        String authToken = jwtUtil.buildToken(
            jwsClaims.getPayload().getSubject(),
            claimsFromYesterday,
            TestUtils.yesterday(),
            jwsClaims.getPayload().getExpiration()
        );

        MockHttpServletResponse response = makeRequest(authToken);

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        Claims originalClaims = jwtUtil.resolveClaims(authToken).getPayload();
        Claims newClaims = jwtUtil.resolveClaims(response.getCookie(Constants.JWT_COOKIE_NAME).getValue()).getPayload();

        assertTrue(newClaims.get(ClaimConstants.SERVER_START, Date.class).after(
            originalClaims.get(ClaimConstants.SERVER_START, Date.class)
        ));
        assertEquals(originalClaims.getExpiration(), newClaims.getExpiration());
    }


    private MockHttpServletResponse makeRequest(String authToken) throws IOException, ServletException
    {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/some-asset");
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.setCookies(new Cookie(Constants.JWT_COOKIE_NAME, authToken));

        jwsFilter.doFilterInternal(request, response, new MockFilterChain());
        return response;
    }
}