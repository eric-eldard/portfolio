package com.eric_eldard.portfolio.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import java.net.URI;

import com.eric_eldard.portfolio.test.BaseMvcIntegrationTest;

public class AdditionalLocationIT extends BaseMvcIntegrationTest
{
    @Test
    @SneakyThrows
    @WithMockUser(authorities = "OLD_PORTFOLIO")
    public void testAuthorityGrantsAccessToAdditionalLocation()
    {
        MvcResult result = getPage(makeOldPortoflioUri())
            .andExpect(status().isFound())
            .andReturn();

        String redirectLocation = result.getResponse().getHeader("Location");
        assertNotNull(redirectLocation);

        getPage(makeOldPortoflioUri(redirectLocation))
            .andExpect(status().isOk());
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
            .andExpect(status().isUnauthorized());
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