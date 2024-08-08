package com.eric_eldard.portfolio.service.video;

public enum EmbeddableVideo
{
    BRAZENITE_GPT("vi5Y7abhpi19t5srd5e1dQmF"),
    HACKATHON("viXjgpvEHRcEuL1XKoQumV8"),
    INTELLIJ_POSTFIX("vi1O4WZbv8koAZ35tNb71YVG"),
    TREE_OF_USAGES("vi6s6yhI9q0x1W0U1WXwwgGn");

    private final String id;

    EmbeddableVideo(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }
}
