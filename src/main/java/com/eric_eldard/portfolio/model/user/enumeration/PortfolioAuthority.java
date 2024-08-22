package com.eric_eldard.portfolio.model.user.enumeration;

public enum PortfolioAuthority
{
    OLD_PORTFOLIO("Old Portfolio Access");

    final String pretty;

    PortfolioAuthority(String pretty)
    {
        this.pretty = pretty;
    }

    public String pretty()
    {
        return pretty;
    }
}