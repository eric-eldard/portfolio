package com.eric_eldard.portfolio.controller.portfolio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import video.api.client.api.ApiException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import com.eric_eldard.portfolio.service.resource.ResourceService;
import com.eric_eldard.portfolio.service.video.EmbeddableVideo;
import com.eric_eldard.portfolio.service.video.EmbeddableVideoService;
import com.eric_eldard.portfolio.util.Constants;
import com.eric_eldard.portfolio.util.StringUtils;

@Controller
@RequestMapping("/portfolio")
public class PortfolioController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PortfolioController.class);

    private final EmbeddableVideoService videoService;

    private final ResourceService resourceService;

    private final String assetsFilePath;

    public PortfolioController(EmbeddableVideoService videoService,
                               ResourceService resourceService,
                               @Value("${portfolio.assets-path}") String assetsFilePath)
    {
        this.videoService = videoService;
        this.resourceService = resourceService;
        this.assetsFilePath = StringUtils.withTrailingString(assetsFilePath, "/");
    }

    @GetMapping
    public String getPortfolio(Model model)
    {
        addFileResources(model, ResourceType.DOCUMENTS);
        addFileResources(model, ResourceType.IMAGES);
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
     * Forwards <tt>/portfolio/logout</tt> to <tt>/logout</tt>
     */
    @GetMapping("/logout")
    public String logout()
    {
        return "forward:/logout";
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
    private void addFileResources(Model model, ResourceType type)
    {
        String pathPattern = type.makePathPattern(assetsFilePath);
        List<Resource> resources;

        try
        {
            resources = resourceService.getFileResources(pathPattern);
        }
        catch (IOException ex)
        {
            LOGGER.error("Unable to load {} resources from the classpath for reason: {}", type, ex.getMessage());
            return;
        }

        List<String> filePaths = resources.stream()
            .map(resourceService::resourceUriToString)
            .filter(Objects::nonNull)
            .map(filePath -> filePath.replace(assetsFilePath, "/"))
            .map(partialPath -> Constants.ASSETS_PATH + partialPath)
            .toList();

        model.addAttribute(type.name(), filePaths);
    }

    private enum ResourceType
    {
        DOCUMENTS("**/*.pdf"),
        IMAGES("**/*.png");

        private final String pattern;

        ResourceType(String pattern)
        {
            this.pattern = pattern;
        }

        public String makePathPattern(String basePath)
        {
            return basePath + pattern;
        }
    }
}