package com.eric_eldard.portfolio.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import com.eric_eldard.portfolio.test.BaseMvcIntegrationTest;

public class AdditionalLocationIT extends BaseMvcIntegrationTest
{
    @Test
    @SneakyThrows
    @WithMockUser(authorities = "OLD_PORTFOLIO")
    public void testAuthorityGrantsAccessToAdditionalLocation()
    {
        // Verify request redirect is issued
        MvcResult redirectResult = getPage(makeOldPortoflioUri())
            .andExpect(status().isFound())
            .andReturn();

        // Verify Location header has redirect url
        String redirectLocation = redirectResult.getResponse().getHeader("Location");
        assertNotNull(redirectLocation);

        // Verify access to redirect location
        MvcResult pageResult = getPage(makeOldPortoflioUri(redirectLocation))
            .andExpect(status().isOk())
            .andReturn();

        // Verify correct content returned from ultimate destination
        String expected = new ClassPathResource("test/portfolio/locations/index.html")
            .getContentAsString(StandardCharsets.UTF_8);
        String actual = pageResult.getResponse()
            .getContentAsString(StandardCharsets.UTF_8);
        assertEquals(expected, actual);
    }


    @Test
    @SneakyThrows
    @WithMockUser
    public void testUserLackingAuthorityDeniedAccessToAdditionalLocation()
    {
        getPage(makeOldPortoflioUri())
            .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    public void testAdminLackingAuthorityDeniedAccessToAdditionalLocation()
    {
        getPage(makeOldPortoflioUri())
            .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void testUnauthenticatedCannotAccessAdditionalLocations()
    {
        getPage(makeOldPortoflioUri())
            .andExpect(status().isFound())
            .andDo(this::assertRedirectToLogin);
    }

    private URI makeOldPortoflioUri()
    {
        return makeOldPortoflioUri("/portfolio/old/test");
    }

    @SneakyThrows
    private URI makeOldPortoflioUri(String path)
    {
        return makeBaseUri()
            .path(path)
            .build()
            .toUri();
    }
}