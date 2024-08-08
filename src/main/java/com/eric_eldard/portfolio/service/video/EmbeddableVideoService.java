package com.eric_eldard.portfolio.service.video;

import video.api.client.api.ApiException;

public interface EmbeddableVideoService
{
    String getVideoIFrame(EmbeddableVideo embeddableVideo)  throws ApiException;
}