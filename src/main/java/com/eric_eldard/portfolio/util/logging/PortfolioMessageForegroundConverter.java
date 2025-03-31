package com.eric_eldard.portfolio.util.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.color.ANSIConstants;
import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase;

public class PortfolioMessageForegroundConverter extends ForegroundCompositeConverterBase<ILoggingEvent>
{
    @Override
    protected String getForegroundColorCode(ILoggingEvent event)
    {
        if (event.getLoggerName().contains("com.eric_eldard"))
        {
            return ANSIConstants.DEFAULT_FG;
        }
        return ANSIConstants.WHITE_FG;
    }
}