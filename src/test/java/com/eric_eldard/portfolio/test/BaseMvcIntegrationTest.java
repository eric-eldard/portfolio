package com.eric_eldard.portfolio.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.inject.Inject;
import java.net.URI;

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
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BaseMvcIntegrationTest
{
    private static final String BASE_URL = "http://localhost:8080";

    @LocalServerPort
    private int port;

    @Inject
    private MockMvc mockMvc;

    @Inject
    private PortfolioUserService userService;


    protected MockMvc mockMvc()
    {
        return mockMvc;
    }

    protected PortfolioUserService userService()
    {
        return userService;
    }

    protected PortfolioUserDto makeNonAdminUser()
    {
        return PortfolioUserDto.builder()
            .username("portfolio-viewer")
            .password("01234567")
            .enabled(true)
            .build();
    }

    protected PortfolioUser makeAndSaveNonAdminUser()
    {
        return userService.create(makeNonAdminUser());
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
    protected ResultActions getPage(URI uri)
    {
        return mockMvc.perform(MockMvcRequestBuilders.get(uri));
    }

    /**
     * Perform an application/json POST to the URI, with a CSRF token, and return ResultActions for continued chaining.
     */
    @SneakyThrows
    protected ResultActions post(URI uri, Object body)
    {
        return mockMvc.perform(MockMvcRequestBuilders.post(uri)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonify(body))
            .with(csrf().asHeader())
        );
    }

    /**
     * Perform an application/json PATCH to the URI, with a CSRF token, and return ResultActions for continued chaining.
     */
    @SneakyThrows
    protected ResultActions patch(URI uri, Object body)
    {
        return mockMvc.perform(MockMvcRequestBuilders.patch(uri)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonify(body))
            .with(csrf().asHeader())
        );
    }

    /**
     * Perform a DELETE op on the URI, with a CSRF token, and return ResultActions for continued chaining.
     */
    @SneakyThrows
    protected ResultActions delete(URI uri)
    {
        return mockMvc.perform(MockMvcRequestBuilders.delete(uri)
            .with(csrf().asHeader())
        );
    }

    protected UriComponentsBuilder makeBaseUri()
    {
        return UriComponentsBuilder
            .fromHttpUrl(BASE_URL)
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