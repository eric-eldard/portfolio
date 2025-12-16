package com.eric_eldard.portfolio.service.video;

import org.apache.commons.lang3.StringUtils;
import video.api.client.ApiVideoClient;
import video.api.client.api.ApiException;
import video.api.client.api.models.Environment;
import video.api.client.api.models.Video;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * An {@link EmbeddableVideo} implementation for working with <a href="https://api.video/">api.video</a>
 */
@Service
public class ApiVideoService implements EmbeddableVideoService
{
    private final String apiVideoKey;
    private final Environment apiVideoEnv;

    public ApiVideoService(@Value("${apiVideo.key}") String apiVideoKey,
                           @Value("${apiVideo.env:PRODUCTION}") Environment apiVideoEnv
    )
    {
        if (StringUtils.isBlank(apiVideoKey))
        {
            throw new IllegalStateException("Cannot start api.video service without a value for property apiVideo.key");
        }
        this.apiVideoKey = apiVideoKey;
        this.apiVideoEnv = apiVideoEnv;
    }

    @Override
    public String getAccessToken(EmbeddableVideo embeddableVideo) throws ApiException
    {
        String videoId = embeddableVideo.getId();
        Video video = makeClient().videos().get(videoId);
        if (video == null || video.getAssets() == null || video.getAssets().getPlayer() == null)
        {
            throw new ApiException("null " + (video == null ? "video" : "player") +" retrieved for video " + videoId);
        }
        String uri = video.getAssets().getPlayer().toString();
        return uri.substring(uri.indexOf("token=") + "token=".length());
    }

    /**
     * Though the underlying {@link okhttp3.OkHttpClient} is thread safe and actually recommends reusing instances,
     * for some reason api.video recommends creating an instance of ApiVideoClient <i>per thread</i>
     * @see <a href="https://github.com/apivideo/api.video-java-client/blob/main/README.md#recommendation">api.video Java API client</a>
     */
    private ApiVideoClient makeClient()
    {
        return new ApiVideoClient(apiVideoKey, apiVideoEnv);
    }
}