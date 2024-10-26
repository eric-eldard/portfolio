package com.eric_eldard.portfolio.controller.admin;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import jakarta.annotation.Nullable;
import java.net.URI;

import org.springframework.test.annotation.DirtiesContext;

import com.eric_eldard.portfolio.test.BaseMvcIntegrationTest;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserPageControllerIT extends BaseMvcIntegrationTest
{
    @Test
    @SneakyThrows
    public void testAdminCanViewAllUsers()
    {
        get(makeUsersUri(), asAdmin())
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void testViewerCannotViewAllUsers()
    {
        get(makeUsersUri(), asPortfolioViewer())
            .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void testUnauthenticatedCannotViewAllUsers()
    {
        get(makeUsersUri(), asUnauthenticated())
            .andExpect(status().isFound())
            .andDo(this::assertRedirectToLogin);
    }


    @Test
    @SneakyThrows
    public void testAdminCanViewUser()
    {
        long userId = makeAndSaveNonAdminUser().getId();
        get(makeUsersUri(userId), asAdmin())
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void testViewerCannotViewUser()
    {
        long userId = makeAndSaveNonAdminUser().getId();
        get(makeUsersUri(userId), asPortfolioViewer())
            .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void testUnauthenticatedCannotViewUser()
    {
        long userId = makeAndSaveNonAdminUser().getId();
        get(makeUsersUri(userId), asUnauthenticated())
            .andExpect(status().isFound())
            .andDo(this::assertRedirectToLogin);
    }


    private URI makeUsersUri()
    {
        return makeUsersUri(null);
    }

    private URI makeUsersUri(@Nullable Long userId)
    {
        return makeBaseUri()
            .path("/portfolio/users" + (userId == null ? "" : "/" + userId))
            .build()
            .toUri();
    }
}