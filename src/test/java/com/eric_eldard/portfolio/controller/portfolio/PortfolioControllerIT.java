package com.eric_eldard.portfolio.controller.portfolio;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.net.URI;

import com.eric_eldard.portfolio.test.BaseMvcIntegrationTest;

public class PortfolioControllerIT extends BaseMvcIntegrationTest
{
    @Test
    @SneakyThrows
    public void testAdminCanViewPortfolio()
    {
        get(makePortoflioUri(), asAdmin())
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void testViewerCanViewPortfolio()
    {
        get(makePortoflioUri(), asPortfolioViewer())
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void testUnauthenticatedCannotViewPortfolio()
    {
        get(makePortoflioUri(), asUnauthenticated())
            .andExpect(status().isFound())
            .andDo(this::assertRedirectToLogin);
    }


    private URI makePortoflioUri()
    {
        return makeBaseUri()
            .path("/portfolio")
            .build()
            .toUri();
    }
}