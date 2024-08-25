package com.eric_eldard.portfolio.util;

public final class StringUtils
{
    /**
     * @return the string with the trailing string appended to the end, unless it already ended in the trailing string
     */
    public static String withTrailingString(String str, String trailingStr)
    {
        if (str.endsWith(trailingStr))
        {
            return str;
        }
        return str + trailingStr;
    }

    /**
     * @return the string with the trailing string removed from the end, if it originally ended with this string
     */
    public static String withoutTrailingString(String str, String trailingStr)
    {
        if (str.endsWith(trailingStr))
        {
            return str.substring(0, str.lastIndexOf(trailingStr));
        }
        return str;
    }

    private StringUtils()
    {
        // util ctor
    }
}