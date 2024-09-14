// Stand-ins for types that will be available at runtime from swipe-events.js

declare type SwipeEventDetail = {
    eventTime:       number;
    duration:        number;
    ongoing:         boolean;
    cardinal4dir:    string;
    cardinal8dir:    string;
    originX:         number;
    originY:         number;
    currentX:        number;
    currentY:        number;
    totalDistanceX:  number;
    totalDistanceY:  number;
    totalDistance:   number;
    latestDistanceX: number;
    latestDistanceY: number;
    latestDistance:  number;
    overallSpeedX:   number;
    overallSpeedY:   number;
    overallSpeed:    number;
    latestSpeedX:    number;
    latestSpeedY:    number;
    latestSpeed:     number;
}

export type SwipeEvent = Event & {
    detail: SwipeEventDetail;
}

export type SwipeEvents = {
    telemetryLoggingEnabled(): boolean;
}