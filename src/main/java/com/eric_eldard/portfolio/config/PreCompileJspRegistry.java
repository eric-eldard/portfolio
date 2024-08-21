package com.eric_eldard.portfolio.config;

import org.apache.tomcat.util.descriptor.web.ServletDef;
import org.apache.tomcat.util.descriptor.web.WebXml;
import org.apache.tomcat.util.descriptor.web.WebXmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xml.sax.InputSource;

import jakarta.servlet.ServletRegistration;
import java.io.InputStream;
import java.util.Map;

/**
 * Reads pre-compiled JSPs locations out of the web.xml and provides their paths to the servlet.
 * Adapted from <a href="https://stackoverflow.com/a/55231198/1908807">https://stackoverflow.com/a/55231198/1908807</a>
 */
@Configuration
public class PreCompileJspRegistry
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PreCompileJspRegistry.class);

    @Bean
    public ServletContextInitializer registerPreCompiledJsps()
    {
        return servletContext ->
        {
            InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/web.xml");
            if (inputStream == null)
            {
                LOGGER.info("Could not read web.xml");
                return;
            }
            try
            {
                WebXmlParser parser = new WebXmlParser(false, false, true);
                WebXml webXml = new WebXml();
                boolean success = parser.parseWebXml(new InputSource(inputStream), webXml, false);

                if (!success)
                {
                    LOGGER.error("Error registering precompiled JSPs");
                    return;
                }

                for (ServletDef def : webXml.getServlets().values())
                {
                    LOGGER.info("Registering precompiled JSP: {} -> {}", def.getServletName(), def.getServletClass());
                    ServletRegistration.Dynamic reg =
                        servletContext.addServlet(def.getServletName(), def.getServletClass());
                    reg.setInitParameter("development", "false");
                    reg.setLoadOnStartup(99);
                }

                for (Map.Entry<String, String> mapping : webXml.getServletMappings().entrySet())
                {
                    LOGGER.info("Mapping servlet: {} -> {}", mapping.getValue(), mapping.getKey());
                    servletContext.getServletRegistration(mapping.getValue()).addMapping(mapping.getKey());
                }
            }
            catch (Exception ex)
            {
                LOGGER.error("Error registering precompiled JSPs", ex);
            }
        };
    }
}