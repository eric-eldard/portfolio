package com.eric_eldard.controller.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.annotation.Nullable;
import jakarta.inject.Inject;
import java.net.URI;
import java.util.Optional;

import com.eric_eldard.TestConfig;
import com.eric_eldard.portfolio.PortfolioApp;
import com.eric_eldard.portfolio.model.user.PortfolioUser;
import com.eric_eldard.portfolio.model.user.PortfolioUserDto;
import com.eric_eldard.portfolio.service.user.PortfolioUserService;

@SpringBootTest(
    classes = {
        TestConfig.class,
        PortfolioApp.class
    },
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
@PropertySource({
    "classpath:application-test.properties",
    "classpath:application.properties"
})
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserManagementIT
{
    @LocalServerPort
    private int port;

    @Inject
    private MockMvc mockMvc;

    @Inject
    private PortfolioUserService userService;


    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    public void testAdminCanCreateUser()
    {
        PortfolioUserDto newUserDto = makeNonAdminUser();

        MvcResult result = mockMvc.perform(post(makeUsersUri())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonify(newUserDto))
                .with(csrf().asHeader())
            )
            .andExpect(status().isCreated())
            .andReturn();

        long userId = Long.valueOf(result.getResponse().getContentAsString());
        Optional<PortfolioUser> createdUser = userService.findById(userId);

        assertTrue(createdUser.isPresent());
        assertEquals(newUserDto.getUsername(),        createdUser.get().getUsername());
        assertEquals(newUserDto.isEnabled(),          createdUser.get().isEnabled());
        assertEquals(newUserDto.isAdmin(),            createdUser.get().isAdmin());
        assertEquals(newUserDto.getAuthorizedUntil(), createdUser.get().getAuthorizedUntil());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void testAuthenticatedViewerCannotCreateUser()
    {
        mockMvc.perform(post(makeUsersUri())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonify(makeNonAdminUser()))
                .with(csrf().asHeader())
            )
            .andExpect(status().isForbidden());

        assertTrue(userService.findAll().isEmpty());
    }

    @Test
    @SneakyThrows
    public void testUnauthenticatedCannotCreateUser()
    {
        mockMvc.perform(post(makeUsersUri())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonify(makeNonAdminUser()))
                .with(csrf().asHeader())
            )
            .andExpect(status().isUnauthorized());

        assertTrue(userService.findAll().isEmpty());
    }


    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    public void testAdminCanDeleteUser()
    {
        long userId = makeAndSaveNonAdminUser().getId();
        mockMvc.perform(delete(makeUsersUri(userId))
                .with(csrf().asHeader())
            )
            .andExpect(status().isOk());

        Optional<PortfolioUser> user = userService.findById(userId);
        assertFalse(user.isPresent());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void testViewerCannotDeleteUser()
    {
        long userId = makeAndSaveNonAdminUser().getId();
        mockMvc.perform(delete(makeUsersUri(userId))
                .with(csrf().asHeader())
            )
            .andExpect(status().isForbidden());

        Optional<PortfolioUser> user = userService.findById(userId);
        assertTrue(user.isPresent());
    }

    @Test
    @SneakyThrows
    public void testUnauthenticatedCannotDeleteUser()
    {
        long userId = makeAndSaveNonAdminUser().getId();
        mockMvc.perform(delete(makeUsersUri(userId))
                .with(csrf().asHeader())
            )
            .andExpect(status().isUnauthorized());

        Optional<PortfolioUser> user = userService.findById(userId);
        assertTrue(user.isPresent());
    }


    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    public void testAdminCanPromoteUser()
    {
        long userId = makeAndSaveNonAdminUser().getId();
        mockMvc.perform(patch(makeUsersUri(userId, "toggle-admin"))
                .with(csrf().asHeader())
            )
            .andExpect(status().isOk());

        PortfolioUser user = userService.findById(userId).get();
        assertTrue(user.isAdmin());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void testViewerCannotPromoteUser()
    {
        long userId = makeAndSaveNonAdminUser().getId();
        mockMvc.perform(delete(makeUsersUri(userId, "toggle-admin"))
                .with(csrf().asHeader())
            )
            .andExpect(status().isForbidden());

        PortfolioUser user = userService.findById(userId).get();
        assertFalse(user.isAdmin());
    }

    @Test
    @SneakyThrows
    public void testUnauthenticatedCannotPromoteUser()
    {
        long userId = makeAndSaveNonAdminUser().getId();
        mockMvc.perform(delete(makeUsersUri(userId, "toggle-admin"))
                .with(csrf().asHeader())
            )
            .andExpect(status().isUnauthorized());

        PortfolioUser user = userService.findById(userId).get();
        assertFalse(user.isAdmin());
    }


    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    public void testAdminCanUpdateUserPassword()
    {
        PortfolioUser user = makeAndSaveNonAdminUser();
        long userId = user.getId();

        mockMvc.perform(patch(makeUsersUri(userId, "password"))
                .contentType(MediaType.TEXT_PLAIN_VALUE)
                .content("abcdefgh")
                .with(csrf().asHeader())
            )
            .andExpect(status().isOk());

        Optional<PortfolioUser> updatedUser = userService.findById(userId);
        assertNotEquals(user.getPassword(), updatedUser.get().getPassword());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void testAuthenticatedViewerCannotUpdateUserPassword()
    {
        PortfolioUser user = makeAndSaveNonAdminUser();
        long userId = user.getId();

        mockMvc.perform(patch(makeUsersUri(userId, "password"))
                .contentType(MediaType.TEXT_PLAIN_VALUE)
                .content("abcdefgh")
                .with(csrf().asHeader())
            )
            .andExpect(status().isForbidden());

        Optional<PortfolioUser> updatedUser = userService.findById(userId);
        assertEquals(user.getPassword(), updatedUser.get().getPassword());
    }

    @Test
    @SneakyThrows
    public void testUnauthenticatedCannotUpdateUserPassword()
    {
        PortfolioUser user = makeAndSaveNonAdminUser();
        long userId = user.getId();
        
        mockMvc.perform(patch(makeUsersUri(userId, "password"))
                .contentType(MediaType.TEXT_PLAIN_VALUE)
                .content("abcdefgh")
                .with(csrf().asHeader())
            )
            .andExpect(status().isUnauthorized());

        Optional<PortfolioUser> updatedUser = userService.findById(userId);
        assertEquals(user.getPassword(), updatedUser.get().getPassword());
    }


    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    public void testCsrfTokenRequired()
    {
        mockMvc.perform(post(makeUsersUri())
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
        mockMvc.perform(post(makeUsersUri())
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

        return UriComponentsBuilder
            .fromHttpUrl("http://localhost")
            .port(port)
            .path(fullPath.toString())
            .build()
            .toUri();
    }

    private PortfolioUserDto makeNonAdminUser()
    {
        return PortfolioUserDto.builder()
            .username("portfolio-viewer")
            .password("01234567")
            .enabled(true)
            .build();
    }

    private PortfolioUser makeAndSaveNonAdminUser()
    {
        return userService.create(makeNonAdminUser());
    }

    @SneakyThrows
    private String jsonify(Object obj)
    {
        return new ObjectMapper().writeValueAsString(obj);
    }
}