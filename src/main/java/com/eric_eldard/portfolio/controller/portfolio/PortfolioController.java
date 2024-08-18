package com.eric_eldard.portfolio.controller.portfolio;

import com.eric_eldard.portfolio.util.Constants;
import com.eric_eldard.portfolio.service.classpath.ClasspathService;
import com.eric_eldard.portfolio.service.video.EmbeddableVideo;
import com.eric_eldard.portfolio.service.video.EmbeddableVideoService;
import com.eric_eldard.portfolio.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import video.api.client.api.ApiException;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/portfolio")
public class PortfolioController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PortfolioController.class);

    private final ClasspathService classpathService;

    private final EmbeddableVideoService videoService;

    public PortfolioController(ClasspathService classpathService, EmbeddableVideoService videoService)
    {
        this.classpathService = classpathService;
        this.videoService = videoService;
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
        addClasspathResources(model, ResourceType.IMAGES);
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
        List<Resource> resources;
        try
        {
            resources = classpathService.getClasspathResources("classpath:" + type.path);
        }
        catch (IOException ex)
        {
            LOGGER.error("Unable to load {} resources from the classpath for reason: {}", type, ex.getMessage());
            return;
        }

        List<String> filePaths = resources.stream()
            .map(classpathService::getUriOfResource)
            .filter(Objects::nonNull)
            .map(URI::toString)
            .map(filePath -> StringUtils.substringAt(filePath, Constants.ASSETS_PATH))
            .toList();

        model.addAttribute(type.name(), filePaths);
    }

    private enum ResourceType
    {
        DOCUMENTS(Constants.ASSETS_PATH + "**/*.pdf"),
        IMAGES(Constants.ASSETS_PATH + "**/*.png");

        private final String path;

        ResourceType(String path)
        {
            this.path = path;
        }
    }
}