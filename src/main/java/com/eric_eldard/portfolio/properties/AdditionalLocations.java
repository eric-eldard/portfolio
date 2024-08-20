package com.eric_eldard.portfolio.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads in and stores additional locations which are not part of the portfolio app, but will still sit behind its
 * security. Specify locations as properties in the format
 * <br><br>
 * {@code portfolio.additional-locations.locations[i]=/web/path=/file/path}
 * <br><br>
 * in which {@code i} is incremented sequentially from {@code 0} with each additional location, {@code /web/path} is
 * the absolute path where the resource will be exposed in the portfolio app, and {@code /file/path} is the absolute
 * location of the resource's base directory in the file system.
 * <br><br>
 * Example:
 * <br><br>
 * {@code portfolio.additional-locations.locations[0]=/portfolio/old=/opt/portfolio/assets/old-portfolio}
 * <br><br>
 * The resource should have an {@code index.html} in its base directory, to which calls to the {@code /web/path} will
 * redirect.
 */
@ConfigurationProperties(prefix = "portfolio.additional-locations")
public class AdditionalLocations
{
    private final Map<String, String> additionalLocationMappings;

    @ConstructorBinding
    public AdditionalLocations(List<String> locations)
    {
        if (locations == null)
        {
            additionalLocationMappings = Collections.emptyMap();
        }
        else
        {
            this.additionalLocationMappings = new HashMap<>(locations.size());
            locations.forEach(location ->
            {
                String[] split = location.split("=");
                additionalLocationMappings.put(split[0], split[1]);
            });
        }
    }

    /**
     * @return an unmodifiable mapping of web path->file path for all additional locations
     */
    public Map<String, String> getMappings()
    {
        return Collections.unmodifiableMap(additionalLocationMappings);
    }
}