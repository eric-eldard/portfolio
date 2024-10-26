package com.eric_eldard.portfolio.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.Cookie;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MvcResult;

import com.eric_eldard.portfolio.test.BaseMvcIntegrationTest;

public class AdditionalLocationIT extends BaseMvcIntegrationTest
{
    @Test
    @SneakyThrows
    public void testAuthorityGrantsAccessToAdditionalLocation()
    {
        Cookie authTokenCookie = asOldPortfolioViewer();

        // Verify request redirect is issued
        MvcResult redirectResult = get(makeOldPortoflioUri(), authTokenCookie)
            .andExpect(status().isFound())
            .andReturn();

        // Verify Location header has redirect url
        String redirectLocation = redirectResult.getResponse().getHeader("Location");
        assertNotNull(redirectLocation);

        // Verify access to redirect location
        MvcResult pageResult = get(makeOldPortoflioUri(redirectLocation), authTokenCookie)
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
    public void testUserLackingAuthorityDeniedAccessToAdditionalLocation()
    {
        get(makeOldPortoflioUri(), asPortfolioViewer())
            .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void testAdminLackingAuthorityDeniedAccessToAdditionalLocation()
    {
        get(makeOldPortoflioUri(), asAdmin())
            .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void testUnauthenticatedCannotAccessAdditionalLocations()
    {
        get(makeOldPortoflioUri(), asUnauthenticated())
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