package com.eric_eldard.portfolio.security;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import java.net.URI;

import com.eric_eldard.portfolio.test.BaseMvcIntegrationTest;

/**
 * Smoke test of access to Spring Actuator endpoints, which are all restricted to admins
 */
public class ActuatorAccessIT extends BaseMvcIntegrationTest
{
    @Test
    public void testAdminCanAccessActuatorLogging() throws Exception
    {
        get(makeActuatorUri("loggers"), asAdmin())
            .andExpect(status().isOk());
    }

    @Test
    public void testNonAdminCannotAccessActuatorLogging() throws Exception
    {
        get(makeActuatorUri("loggers"), asPortfolioViewer())
            .andExpect(status().isForbidden());
    }

    @Test
    public void testUnauthenticatedCannotAccessActuatorLogging() throws Exception
    {
        get(makeActuatorUri("loggers"), asUnauthenticated())
            .andExpect(status().isFound())
            .andDo(this::assertRedirectToLogin);
    }

    private URI makeActuatorUri(String path)
    {
        return makeBaseUri()
            .path("/actuator/" + path)
            .build()
            .toUri();
    }
}