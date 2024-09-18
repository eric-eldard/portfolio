// Stand-ins for types that will be available at runtime from swipe-events.js

declare type SwipeEventDetail = {
    event:           TouchEvent;
    eventTime:       number;
    duration:        number;
    initial:         boolean;
    ongoing:         boolean;
    cardinal4:       string;
    cardinal8:       string;
    theta:           number;
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