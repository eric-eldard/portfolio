package com.eric_eldard.portfolio.controller.portfolio;

import com.eric_eldard.portfolio.service.classpath.ClasspathService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.eric_eldard.portfolio.service.video.EmbeddableVideo;
import com.eric_eldard.portfolio.service.video.EmbeddableVideoService;
import video.api.client.api.ApiException;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/portfolio")
public class PortfolioController implements WebMvcConfigurer
{
    private static final String ASSETS_PATH = "/portfolio/assets/";

    private static final Logger LOGGER = LoggerFactory.getLogger(PortfolioController.class);

    private final ClasspathService classpathService;

    private final EmbeddableVideoService videoService;

    @Inject
    public PortfolioController(ClasspathService classpathService, EmbeddableVideoService videoService)
    {
        this.classpathService = classpathService;
        this.videoService = videoService;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        // Exposes protected assets to any authenticated user
        registry.addResourceHandler("/portfolio/assets/**")
            .addResourceLocations("classpath:" + ASSETS_PATH)
            .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));

        // Public assets already exposed; this adds cache control to public images
        registry.addResourceHandler("/public/assets/images/**")
            .addResourceLocations("/public/assets/images/")
            .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));
    }

    @GetMapping("")
    public String forwardNoSlash()
    {
        return "redirect:portfolio/";
    }

    @GetMapping("/")
    public String getPortfolio(Model model)
    {
        addClasspathResources(model, ResourceType.DOCUMENTS);
        addClasspathResources(model, ResourceType.SCREENSHOTS);
        return "portfolio";
    }

    /**
     * Generic endpoint for serving any JSP that has no dynamic content
     */
    @GetMapping("/content/{content}")
    public String getStaticPortfolioContent(@PathVariable String content)
    {
        return "content/" + content;
    }

    @GetMapping("/content/executive")
    public String getExecutiveContent(Model model) throws ApiException
    {
        model.addAttribute(
            "intellijPostfix",
            retrieveVideoIframe(EmbeddableVideo.INTELLIJ_POSTFIX)
        );
        model.addAttribute(
            "hackathon",
            retrieveVideoIframe(EmbeddableVideo.HACKATHON)
        );
        return "content/executive";
    }

    @GetMapping("/content/software-engineer")
    public String getSoftwareEngineerContent(Model model) throws ApiException
    {
        model.addAttribute(
            "brazeniteGpt",
            retrieveVideoIframe(EmbeddableVideo.BRAZENITE_GPT)
        );
        model.addAttribute(
            "treeOfUsages",
            retrieveVideoIframe(EmbeddableVideo.TREE_OF_USAGES)
        );
        return "content/software-engineer";
    }

    /**
     * Get a pre-baked iframe for the given video from the video vendor
     */
    private String retrieveVideoIframe(EmbeddableVideo embeddableVideo)
    {
        String iframe;
        try
        {
            iframe = videoService.getVideoIFrame(embeddableVideo);
        }
        catch (ApiException ex)
        {
            LOGGER.error(
                "Unable to retrieve video [{}] for reason [{}]",
                embeddableVideo, ex.getMessage()
            );
            iframe = """
                <div class="mono">An error prevented the retrieval of this video</div>
                """;
        }
        return iframe;
    }

    /**
     * Adds paths to the model for all the resources of the given type; supports preloading assets on the browser side
     */
    private void addClasspathResources(Model model, ResourceType type)
    {
        try
        {
            String folder = ASSETS_PATH + type.location();

            List<Resource> resources =
                classpathService.getClasspathResources("classpath:" + folder + "/*");

            List<String> filePaths = resources.stream()
                .map(resource -> folder + '/' + resource.getFilename())
                .toList();

            model.addAttribute(type.name(), filePaths);
        }
        catch (IOException ex)
        {
            LOGGER.warn("Unable to load {} resources from the classpath for reason: {}", type, ex.getMessage());
        }
    }

    private enum ResourceType
    {
        DOCUMENTS("documents"),
        SCREENSHOTS("images/screenshots");

        private final String location;

        ResourceType(String location)
        {
            this.location = location;
        }

        public String location()
        {
            return location;
        }
    }
}