package com.eric_eldard.portfolio.util;

public final class StringUtils
{
    /**
     * @return the substring of {@code #str} starting from the beginning of {#code atStr}
     */
    public static String substringAt(String str, String atStr)
    {
        return str.substring(str.indexOf(atStr));
    }

    private StringUtils()
    {
        // util ctor
    }
}