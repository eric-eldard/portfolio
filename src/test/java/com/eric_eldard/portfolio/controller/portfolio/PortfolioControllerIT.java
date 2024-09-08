package com.eric_eldard.portfolio.controller.portfolio;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.net.URI;

import org.springframework.security.test.context.support.WithMockUser;

import com.eric_eldard.portfolio.test.BaseMvcIntegrationTest;

public class PortfolioControllerIT extends BaseMvcIntegrationTest
{
    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    public void testAdminCanViewPortfolio()
    {
        getPage(makePortoflioUri())
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void testViewerCanViewPortfolio()
    {
        getPage(makePortoflioUri())
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void testUnauthenticatedCannotViewPortfolio()
    {
        getPage(makePortoflioUri())
            .andExpect(status().isFound())
            .andDo(this::assertRedirectToLogin);
    }


    @SneakyThrows
    private URI makePortoflioUri()
    {
        return makeBaseUri()
            .path("/portfolio")
            .build()
            .toUri();
    }
}