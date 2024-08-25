package com.eric_eldard.portfolio.controller.admin;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;

import jakarta.annotation.Nullable;
import java.net.URI;

import com.eric_eldard.portfolio.test.BaseMvcIntegrationTest;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserPageControllerIT extends BaseMvcIntegrationTest
{
    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    public void testAdminCanViewAllUsers()
    {
        getPage(makeUsersUri())
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void testViewerCannotViewAllUsers()
    {
        getPage(makeUsersUri())
            .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void testUnauthenticatedCannotViewAllUsers()
    {
        getPage(makeUsersUri())
            .andExpect(status().isUnauthorized());
    }


    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    public void testAdminCanViewUser()
    {
        long userId = makeAndSaveNonAdminUser().getId();
        getPage(makeUsersUri(userId))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void testViewerCannotViewUser()
    {
        long userId = makeAndSaveNonAdminUser().getId();
        getPage(makeUsersUri(userId))
            .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void testUnauthenticatedCannotViewUser()
    {
        long userId = makeAndSaveNonAdminUser().getId();
        getPage(makeUsersUri(userId))
            .andExpect(status().isUnauthorized());
    }


    private URI makeUsersUri()
    {
        return makeUsersUri(null);
    }

    @SneakyThrows
    private URI makeUsersUri(@Nullable Long userId)
    {
        return makeBaseUri()
            .path("/portfolio/users" + (userId == null ? "" : "/" + userId))
            .build()
            .toUri();
    }
}