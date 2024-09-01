const swipeEvents = (function() {

    let logEvents = false;
    let touchStartX;
    let touchStartY;
    let lastTouchX;
    let lastTouchY;
    let lastEventTime;

    document.addEventListener("touchstart", e => {
        touchStartX   = e.changedTouches[0].screenX;
        touchStartY   = e.changedTouches[0].screenY;
        lastTouchX    = touchStartX;
        lastTouchY    = touchStartY;
        lastEventTime = Date.now();
    });

    document.addEventListener("touchmove", e => {
        const touchCurrentX = e.changedTouches[0].screenX;
        const touchCurrentY = e.changedTouches[0].screenY;
        const eventTime     = Date.now();

        fireSwipeEvent(touchStartX, touchStartY, touchCurrentX, touchCurrentY, eventTime, true);

        lastTouchX    = touchCurrentX;
        lastTouchY    = touchCurrentY;
        lastEventTime = eventTime;
    });

    document.addEventListener("touchend", e => {
        const touchEndX = e.changedTouches[0].screenX;
        const touchEndY = e.changedTouches[0].screenY;
        fireSwipeEvent(touchStartX, touchStartY, touchEndX, touchEndY, Date.now(), false);
    });

    document.addEventListener("touchcancel", e => {
        const touchEndX = e.changedTouches[0].screenX;
        const touchEndY = e.changedTouches[0].screenY;
        fireSwipeEvent(touchStartX, touchStartY, touchEndX, touchEndY, Date.now(), false);
    });

    function fireSwipeEvent(startX, startY, endX, endY, eventTime, ongoing) {
        const totalDistanceX = Math.abs(endX - startX);
        const totalDistanceY = Math.abs(endY - startY);
        const totalDistance  =  Math.hypot(totalDistanceX, totalDistanceY);

        const latestDistanceX = Math.abs(endX - lastTouchX);
        const latestDistanceY = Math.abs(endY - lastTouchY);
        const latestDistance  = Math.hypot(latestDistanceX, latestDistanceY);

        const millisSinceLastEvent = eventTime - lastEventTime;
        const speedX = latestDistanceX / millisSinceLastEvent;
        const speedY = latestDistanceY / millisSinceLastEvent;
        const speed  = latestDistance  / millisSinceLastEvent;

        const horizontalDir = startX > endX ? "W" : "E";
        const verticalDir   = startY > endY ? "N" : "S";
        const theta         = Math.atan2(totalDistanceY, totalDistanceX) * (180 / Math.PI);

        const cardinal4dir = (totalDistanceX > totalDistanceY) ? horizontalDir : verticalDir;
        const cardinal8dir = (theta > 22.5 && theta < 67.5) ? verticalDir + horizontalDir : cardinal4dir;

        document.dispatchEvent(
            new CustomEvent("swipe", {
                detail: {
                    "eventTime":       eventTime,
                    "cardinal4dir":    cardinal4dir,
                    "cardinal8dir":    cardinal8dir,
                    "ongoing":         ongoing,
                    "touchStartX":     startX,
                    "touchStartY":     startY,
                    "touchEndX":       endX,
                    "touchEndY":       endY,
                    "totalDistanceX":  totalDistanceX,
                    "totalDistanceY":  totalDistanceY,
                    "totalDistance":   totalDistance,
                    "latestDistanceX": latestDistanceX,
                    "latestDistanceY": latestDistanceY,
                    "latestDistance":  latestDistance,
                    "speedX":          speedX,
                    "speedY":          speedY,
                    "speed":           speed
                }
            })
        );
    }

    document.addEventListener("swipe", e => {
        if (logEvents) {
            console.debug(`
                -- swipe event --

              %cevent time:            ${e.detail.eventTime}
                ongoing:               ${e.detail.ongoing}
              %ccardinal 4 direction:  ${e.detail.cardinal4dir}
                cardinal 8 direction:  ${e.detail.cardinal8dir}
              %ctouch start X:         ${e.detail.touchStartX}
                touch start Y:         ${e.detail.touchStartY}
              %ctouch end X:           ${e.detail.touchEndX}
                touch end Y:           ${e.detail.touchEndY}
              %ctotal distance X:      ${e.detail.totalDistanceX}
                total distance Y:      ${e.detail.totalDistanceY}
                total distance:        ${e.detail.totalDistance}
              %clatest distance X:     ${e.detail.latestDistanceX}
                latest distance Y:     ${e.detail.latestDistanceY}
                latest distance:       ${e.detail.latestDistance}
              %cspeed X:               ${e.detail.speedX}
                speed Y:               ${e.detail.speedY}
                speed:                 ${e.detail.speed}

            `.replace(/\n[ ]+/g, "\n"),
            "color: dimgray", "color: brown", "color: green", "color: red", "color: blue", "color: orange", "color: purple")
        }
    });

    return {
        telemetryLoggingEnabled: function() {
            return logEvents;
        },

        toggleTelemetryLogging: function() {
            logEvents = !logEvents;
        }
    }
})();