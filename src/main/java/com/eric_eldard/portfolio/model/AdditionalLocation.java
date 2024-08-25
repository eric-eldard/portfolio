package com.eric_eldard.portfolio.model;

import com.eric_eldard.portfolio.model.user.enumeration.PortfolioAuthority;

/**
 * Additional resources which are not part of the portfolio app, but will still sit behind its security.
 * @param webPath the absolute path where the resource will be exposed in the portfolio app
 * @param basePath the absolute location of the resource's base directory (web, file system, or classpath)
 * @param authority the granted {@link PortfolioAuthority} necessary to access the location
 */
public record AdditionalLocation(String webPath, String basePath, PortfolioAuthority authority) {}