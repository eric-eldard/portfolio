package com.eric_eldard.portfolio.service.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Date;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.eric_eldard.portfolio.PortfolioApp;
import com.eric_eldard.portfolio.model.user.PortfolioUser;
import com.eric_eldard.portfolio.model.user.PortfolioUserDto;
import com.eric_eldard.portfolio.model.user.enumeration.PortfolioAuthority;
import com.eric_eldard.portfolio.persistence.user.PortfolioUserRepository;
import com.eric_eldard.portfolio.security.csrf.PortfolioCsrfTokenRepository;
import com.eric_eldard.portfolio.service.user.PortfolioUserService;
import com.eric_eldard.portfolio.test.TestConfig;
import com.eric_eldard.portfolio.test.TestUtils;

@SpringBootTest(
    classes = {
        TestConfig.class,
        PortfolioApp.class
    }
)
@ActiveProfiles("test")
public class PortfolioUserServiceTest
{
    @SpyBean
    private AuthenticationService authenticationService;

    @SpyBean
    private PortfolioCsrfTokenRepository csrfTokenRepo;

    @SpyBean
    private PortfolioUserRepository userRepo;

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private PortfolioUserService userService;


    /**
     * Much faster than {@link DirtiesContext}
     */
    @AfterEach
    public void deleteAllUsers()
    {
        userRepo.deleteAll();
    }


    @Test
    public void testCreateCorrectlyTranscribesDto()
    {
        PortfolioUserDto dto = TestUtils.makePortfolioUserDto();
        PortfolioUser user = userService.create(dto);

        assertEquals(1, userRepo.count());

        assertNotNull(user.getId());
        assertEquals(dto.getUsername(),        user.getUsername());
        assertEquals(dto.isEnabled(),          user.isEnabled());
        assertEquals(dto.isAdmin(),            user.isAdmin());
        assertEquals(dto.getAuthorizedUntil(), user.getAuthorizedUntil());
        assertTrue(passwordEncoder.matches(dto.getPassword(), user.getPassword()));
    }

    @Test
    public void testCreateForAdminMakesUserAnAdmin()
    {
        PortfolioUserDto dto = TestUtils.makePortfolioUserDto();
        dto.setAdmin(true);
        PortfolioUser user = userService.create(dto);
        assertTrue(user.isAdmin());
    }

    @Test
    public void testCreateFailsForBlankUsername()
    {
        PortfolioUserDto portfolioUserDto = TestUtils.makePortfolioUserDto();
        portfolioUserDto.setUsername("   ");
        assertThrows(
            IllegalArgumentException.class,
            () -> userService.create(portfolioUserDto)
        );
        assertTrue(userRepo.findAll().isEmpty());
    }

    @Test
    public void testCreateFailsForExistingUsername()
    {
        userService.create(TestUtils.makePortfolioUserDto());
        assertThrows(
            IllegalArgumentException.class,
            () -> userService.create(TestUtils.makePortfolioUserDto())
        );
        assertEquals(1, userRepo.count());
    }

    @Test
    public void testCreateFailsForNoPassword()
    {
        PortfolioUserDto portfolioUserDto = TestUtils.makePortfolioUserDto();
        portfolioUserDto.setPassword(null);
        assertThrows(
            IllegalArgumentException.class,
            () -> userService.create(portfolioUserDto)
        );
        assertTrue(userRepo.findAll().isEmpty());
    }

    @Test
    public void testCreateFailsForShortPassword()
    {
        PortfolioUserDto portfolioUserDto = TestUtils.makePortfolioUserDto();
        portfolioUserDto.setPassword(TestUtils.makeShortPassword());
        assertThrows(
            IllegalArgumentException.class,
            () -> userService.create(portfolioUserDto)
        );
        assertTrue(userRepo.findAll().isEmpty());
    }

    @Test
    public void testDeleteUser()
    {
        PortfolioUser user = userService.create(TestUtils.makePortfolioUserDto());

        userService.delete(user.getId());

        verify(userRepo).delete(user);
        verify(authenticationService).requireFreshClaimsForUser(user.getId());
        verify(csrfTokenRepo).invalidateForUser(user.getId());

        assertTrue(userRepo.findAll().isEmpty());
    }

    @Test
    @Transactional
    public void testUnlockUser()
    {
        PortfolioUser user = userService.create(TestUtils.makePortfolioUserDto());
        user.setLockedOn(new Date());
        user.setFailedPasswordAttempts(10);
        userRepo.save(user);

        Mockito.reset(userRepo);

        userService.unlock(user.getId());

        verify(userRepo).save(user);
        verify(authenticationService).requireFreshClaimsForUser(user.getId());

        user = userRepo.findById(user.getId()).orElseThrow();

        assertNull(user.getLockedOn());
        assertEquals(0, user.getFailedPasswordAttempts());
    }

    @Test
    public void testSetPassword()
    {
        PortfolioUser user = userService.create(TestUtils.makePortfolioUserDto());
        Mockito.reset(userRepo);

        userService.setPassword(user.getId(), "new-password");

        verify(userRepo).save(user);
        verify(authenticationService).requireFreshClaimsForUser(user.getId());
        verify(csrfTokenRepo).invalidateForUser(user.getId());

        user = userRepo.findById(user.getId()).orElseThrow();

        assertTrue(passwordEncoder.matches("new-password", user.getPassword()));
    }

    @Test
    public void testSetPasswordFailsForShortPassword()
    {
        PortfolioUserDto dto = TestUtils.makePortfolioUserDto();
        PortfolioUser user = userService.create(dto);
        Long userId = user.getId();

        Mockito.reset(userRepo);

        assertThrows(
            IllegalArgumentException.class,
            () -> userService.setPassword(userId, TestUtils.makeShortPassword())
        );

        verify(userRepo, never()).save(user);
        verify(authenticationService, never()).requireFreshClaimsForUser(userId);

        user = userRepo.findById(userId).orElseThrow();

        assertTrue(passwordEncoder.matches(dto.getPassword(), user.getPassword())); // assert password unchanged
    }

    @Test
    public void testSetAuthorizedUntil()
    {
        PortfolioUser user = userService.create(TestUtils.makePortfolioUserDto());
        Mockito.reset(userRepo);

        Date twoYearsFromNow = new Date(user.getAuthorizedUntil().getTime() * 2);
        userService.setAuthorizedUntil(user.getId(), twoYearsFromNow);

        verify(userRepo).save(user);
        verify(authenticationService).requireFreshClaimsForUser(user.getId());

        user = userRepo.findById(user.getId()).orElseThrow();

        assertEquals(twoYearsFromNow, user.getAuthorizedUntil());
    }

    @Test
    public void testSetInfiniteAuthorization()
    {
        PortfolioUser user = userService.create(TestUtils.makePortfolioUserDto());
        Mockito.reset(userRepo);

        userService.setInfiniteAuthorization(user.getId());

        verify(userRepo).save(user);
        verify(authenticationService).requireFreshClaimsForUser(user.getId());

        user = userRepo.findById(user.getId()).orElseThrow();

        assertNull(user.getAuthorizedUntil());
    }

    @Test
    public void testSetEnabled()
    {
        PortfolioUser user = userService.create(TestUtils.makePortfolioUserDto());
        Mockito.reset(userRepo);

        userService.setEnabled(user.getId(), false);

        verify(userRepo).save(user);

        user = userRepo.findById(user.getId()).orElseThrow();

        assertFalse(user.isEnabled());
    }

    @Test
    public void testSetAdmin()
    {
        PortfolioUser user = userService.create(TestUtils.makePortfolioUserDto());
        Mockito.reset(userRepo);

        userService.setIsAdmin(user.getId(), true);

        verify(userRepo).save(user);
        verify(csrfTokenRepo).invalidateForUser(user.getId());

        user = userRepo.findById(user.getId()).orElseThrow();

        assertTrue(user.isAdmin());
    }

    @Test
    public void testToggleAuthAddsAuth()
    {
        PortfolioUser user = userService.create(TestUtils.makePortfolioUserDto());
        Mockito.reset(userRepo);

        userService.toggleAuth(user.getId(), PortfolioAuthority.OLD_PORTFOLIO);

        verify(userRepo).save(user);

        user = userRepo.findWithAuthoritiesById(user.getId()).orElseThrow();

        assertTrue(user.getPortfolioAuthorities().contains(PortfolioAuthority.OLD_PORTFOLIO));
    }

    @Test
    @Transactional
    public void testToggleAuthRemovesAuth()
    {
        PortfolioUser user = userService.create(TestUtils.makePortfolioUserDto());
        user.setPortfolioAuthorities(Sets.newHashSet(PortfolioAuthority.OLD_PORTFOLIO));
        userRepo.save(user);
        Mockito.reset(userRepo);

        userService.toggleAuth(user.getId(), PortfolioAuthority.OLD_PORTFOLIO);

        verify(userRepo).save(user);

        user = userRepo.findWithAuthoritiesById(user.getId()).orElseThrow();

        assertTrue(user.getPortfolioAuthorities().isEmpty());
    }
}