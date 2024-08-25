package com.eric_eldard.portfolio.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.eric_eldard.portfolio.model.AdditionalLocation;
import com.eric_eldard.portfolio.model.user.enumeration.PortfolioAuthority;

/**
 * Reads in and stores {@link AdditionalLocation}s which are not part of the portfolio app, but will still sit behind
 * its security. Specify locations as properties in the format
 * <br><br>
 * {@code portfolio.additional-locations.locations[i]=/web/path | /web/or/file/or/classpath/redirect | PORTFOLIO_AUTHORITY}
 * <br><br>
 * in which {@code i} is incremented sequentially from {@code 0} with each additional location,
 * {@code /web/path} is the absolute path where the resource will be exposed in the portfolio app,
 * {@code /web/or/file/or/classpath/redirect} is the absolute URI of the base directory (web, local file, or classpath)
 * to give access to, and {@code PORTFOLIO_AUTHORITY} is a value of {@link PortfolioAuthority}.
 * <br><br>
 * Example:
 * <br><br>
 * {@code portfolio.additional-locations.locations[0]=/portfolio/old | file:/opt/portfolio/assets/old-portfolio | OLD_PORTFOLIO}
 * <br><br>
 * The resource should have an {@code index.html} in its base directory, to which calls to the {@code /web/path} will
 * redirect.
 */
@Getter
@ConfigurationProperties(prefix = "portfolio.additional-locations")
public class AdditionalLocations
{
    private final Set<AdditionalLocation> locations;

    @ConstructorBinding
    public AdditionalLocations(List<String> locations)
    {
        if (locations == null)
        {
            this.locations = Set.of();
        }
        else
        {
            this.locations = locations.stream()
                .map(location ->
                {
                    String[] split = location.split(" \\| ");
                    return new AdditionalLocation(split[0], split[1], PortfolioAuthority.valueOf(split[2]));
                })
                .collect(Collectors.toUnmodifiableSet());
        }
    }
}