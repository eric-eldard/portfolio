package com.eric_eldard.portfolio.service.video;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import video.api.client.ApiVideoClient;
import video.api.client.api.ApiException;
import video.api.client.api.models.Environment;
import video.api.client.api.models.Video;

import javax.inject.Inject;

@Service
public class ApiVideoService implements EmbeddableVideoService
{
    private final ApiVideoClient apiVideoClient;

    @Inject
    public ApiVideoService(
        @Value("${apiVideo.key}") String apiVideoKey,
        @Value("${apiVideo.env:PRODUCTION}") Environment apiVideoEnv
    )
    {
        if (StringUtils.isBlank(apiVideoKey))
        {
            throw new IllegalStateException("Cannot start api.video service without a value for property apiVideo.key");
        }
        apiVideoClient = new ApiVideoClient(apiVideoKey, apiVideoEnv);
    }

    @Override
    public String getVideoIFrame(EmbeddableVideo embeddableVideo) throws ApiException
    {
        String videoId = embeddableVideo.getId();
        Video video = apiVideoClient.videos().get(videoId);
        if (video == null || video.getAssets() == null)
        {
            throw new ApiException("null " + (video == null ? "video" : "assets") + " retrieved for video " + videoId);
        }
        return video.getAssets().getIframe();
    }
}