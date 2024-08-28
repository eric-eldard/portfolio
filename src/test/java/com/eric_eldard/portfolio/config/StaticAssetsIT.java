package com.eric_eldard.portfolio.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import com.eric_eldard.portfolio.test.BaseMvcIntegrationTest;
import com.eric_eldard.portfolio.util.Constants;

public class StaticAssetsIT extends BaseMvcIntegrationTest
{
    @Test
    @SneakyThrows
    @WithMockUser
    public void testViewersCanAccessStaticAssets()
    {
        // Verify access
        MvcResult imgResult = getPage(makeAssetsUri())
            .andExpect(status().isOk())
            .andReturn();

        // Verify correct content returned
        String expected = new ClassPathResource("test/portfolio/assets/lamp.png")
            .getContentAsString(StandardCharsets.UTF_8);
        String actual = imgResult.getResponse()
            .getContentAsString(StandardCharsets.UTF_8);
        assertEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    public void testUnauthenticatedCannotAccessStaticAssets()
    {
        getPage(makeAssetsUri())
            .andExpect(status().isFound())
            .andDo(this::assertRedirectToLogin);
    }

    @SneakyThrows
    private URI makeAssetsUri()
    {
        return makeBaseUri()
            .path(Constants.ASSETS_PATH + "/lamp.png")
            .build()
            .toUri();
    }
}