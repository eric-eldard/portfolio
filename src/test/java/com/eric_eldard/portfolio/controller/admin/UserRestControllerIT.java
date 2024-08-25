package com.eric_eldard.portfolio.controller.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import jakarta.annotation.Nullable;
import jakarta.inject.Inject;
import java.net.URI;
import java.util.Optional;

import com.eric_eldard.portfolio.test.BaseMvcIntegrationTest;
import com.eric_eldard.portfolio.model.user.PortfolioUser;
import com.eric_eldard.portfolio.model.user.PortfolioUserDto;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserRestControllerIT extends BaseMvcIntegrationTest
{
    @Inject
    private PasswordEncoder passwordEncoder;


    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    public void testAdminCanCreateUser()
    {
        PortfolioUserDto newUserDto = makeNonAdminUser();

        MvcResult result = post(makeUsersUri(), newUserDto)
            .andExpect(status().isCreated())
            .andReturn();

        long userId = Long.valueOf(result.getResponse().getContentAsString());
        Optional<PortfolioUser> createdUser = userService().findById(userId);

        assertTrue(createdUser.isPresent());
        assertEquals(newUserDto.getUsername(),                       createdUser.get().getUsername());
        assertEquals(newUserDto.isEnabled(),                         createdUser.get().isEnabled());
        assertEquals(newUserDto.isAdmin(),                           createdUser.get().isAdmin());
        assertEquals(newUserDto.getAuthorizedUntil(),                createdUser.get().getAuthorizedUntil());
        assertTrue(passwordEncoder.matches(newUserDto.getPassword(), createdUser.get().getPassword()));
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void testAuthenticatedViewerCannotCreateUser()
    {
        post(makeUsersUri(), makeNonAdminUser())
            .andExpect(status().isForbidden());

        assertTrue(userService().findAll().isEmpty());
    }

    @Test
    @SneakyThrows
    public void testUnauthenticatedCannotCreateUser()
    {
        post(makeUsersUri(), makeNonAdminUser())
            .andExpect(status().isUnauthorized());

        assertTrue(userService().findAll().isEmpty());
    }


    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    public void testAdminCanDeleteUser()
    {
        long userId = makeAndSaveNonAdminUser().getId();
        delete(makeUsersUri(userId))
            .andExpect(status().isOk());

        Optional<PortfolioUser> user = userService().findById(userId);
        assertFalse(user.isPresent());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void testViewerCannotDeleteUser()
    {
        long userId = makeAndSaveNonAdminUser().getId();
        delete(makeUsersUri(userId))
            .andExpect(status().isForbidden());

        Optional<PortfolioUser> user = userService().findById(userId);
        assertTrue(user.isPresent());
    }

    @Test
    @SneakyThrows
    public void testUnauthenticatedCannotDeleteUser()
    {
        long userId = makeAndSaveNonAdminUser().getId();
        delete(makeUsersUri(userId))
            .andExpect(status().isUnauthorized());

        Optional<PortfolioUser> user = userService().findById(userId);
        assertTrue(user.isPresent());
    }


    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    public void testAdminCanPromoteUser()
    {
        long userId = makeAndSaveNonAdminUser().getId();
        patch(
            makeUsersUri(userId, "admin"),
            PortfolioUserDto.builder().admin(true).build()
        ).andExpect(status().isOk());

        PortfolioUser user = userService().findById(userId).get();
        assertTrue(user.isAdmin());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void testViewerCannotPromoteUser()
    {
        long userId = makeAndSaveNonAdminUser().getId();
        patch(
            makeUsersUri(userId, "admin"),
            PortfolioUserDto.builder().admin(true).build()
        ).andExpect(status().isForbidden());

        PortfolioUser user = userService().findById(userId).get();
        assertFalse(user.isAdmin());
    }

    @Test
    @SneakyThrows
    public void testUnauthenticatedCannotPromoteUser()
    {
        long userId = makeAndSaveNonAdminUser().getId();
        patch(
            makeUsersUri(userId, "admin"),
            PortfolioUserDto.builder().admin(true).build()
        ).andExpect(status().isUnauthorized());

        PortfolioUser user = userService().findById(userId).get();
        assertFalse(user.isAdmin());
    }


    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    public void testAdminCanUpdateUserPassword()
    {
        PortfolioUser user = makeAndSaveNonAdminUser();
        long userId = user.getId();

        patch(
            makeUsersUri(userId, "password"),
            PortfolioUserDto.builder().password("abcdefgh").build()
        ).andExpect(status().isOk());

        Optional<PortfolioUser> updatedUser = userService().findById(userId);
        assertNotEquals(user.getPassword(), updatedUser.get().getPassword());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    public void testPasswordMustHave8Chars()
    {
        PortfolioUser user = makeAndSaveNonAdminUser();
        long userId = user.getId();

        patch(
            makeUsersUri(userId, "password"),
            PortfolioUserDto.builder().password("abcdefg").build()
        ).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void testAuthenticatedViewerCannotUpdateUserPassword()
    {
        PortfolioUser user = makeAndSaveNonAdminUser();
        long userId = user.getId();

        patch(
            makeUsersUri(userId, "password"),
            PortfolioUserDto.builder().password("abcdefgh").build()
        ).andExpect(status().isForbidden());

        Optional<PortfolioUser> updatedUser = userService().findById(userId);
        assertEquals(user.getPassword(), updatedUser.get().getPassword());
    }

    @Test
    @SneakyThrows
    public void testUnauthenticatedCannotUpdateUserPassword()
    {
        PortfolioUser user = makeAndSaveNonAdminUser();
        long userId = user.getId();

        patch(
            makeUsersUri(userId, "password"),
            PortfolioUserDto.builder().password("abcdefgh").build()
        ).andExpect(status().isUnauthorized());

        Optional<PortfolioUser> updatedUser = userService().findById(userId);
        assertEquals(user.getPassword(), updatedUser.get().getPassword());
    }


    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    public void testCsrfTokenRequired()
    {
        mockMvc().perform(MockMvcRequestBuilders.post(makeUsersUri())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonify(makeNonAdminUser()))
            )
            .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    public void testValidCsrfTokenRequired()
    {
        mockMvc().perform(MockMvcRequestBuilders.post(makeUsersUri())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonify(makeNonAdminUser()))
                .with(csrf().useInvalidToken().asHeader())
            )
            .andExpect(status().isForbidden());
    }


    private URI makeUsersUri()
    {
        return makeUsersUri(null, null);
    }

    private URI makeUsersUri(Long userId)
    {
        return makeUsersUri(userId, null);
    }

    @SneakyThrows
    private URI makeUsersUri(@Nullable Long userId, @Nullable String operation)
    {
        StringBuilder fullPath = new StringBuilder("/portfolio/users");
        if (userId != null)
        {
            fullPath.append('/').append(userId);
        }
        if (operation != null)
        {
            fullPath.append('/').append(operation);
        }

        return makeBaseUri()
            .path(fullPath.toString())
            .build()
            .toUri();
    }
}