package com.eric_eldard.portfolio.service.video;

import lombok.Getter;

/**
 * IDs of embeddable videos hosted by the video vendor
 */
@Getter
public enum EmbeddableVideo
{
    BRAZENITE_GPT("vi5Y7abhpi19t5srd5e1dQmF"),
    HACKATHON("viXjgpvEHRcEuL1XKoQumV8"),
    INTELLIJ_POSTFIX("vi1O4WZbv8koAZ35tNb71YVG"),
    RESPONSIVE_DESIGN("vi7T9f7zKAVD17wBWKCQh5Wd"),
    TREE_OF_USAGES("vi6s6yhI9q0x1W0U1WXwwgGn");

    private final String id;

    EmbeddableVideo(String id)
    {
        this.id = id;
    }
}