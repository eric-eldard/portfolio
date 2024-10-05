import { Video } from "./video";
import type { SwipeEvent } from "./types/swipe-events";
import type { SwipeEvents } from "./types/swipe-events";

export namespace Portfolio {

    // Will be available at runtime from swipe-events.js
    declare const SwipeEvents: SwipeEvents;

    const SWIPE_SUBSCRIBE_FREQUENCY: number = 1000 / 120; // 120fps necessary for smooth (non-jittery) animation on iOS Safari
    const MAX_POPUP_ROTATION: number = 35;
    const HASH_PATH_KEY: string = "hashPath";

    // Detects when videos have scrolled off screen in order to pause them
    let videoPlayerObserver: IntersectionObserver = createVideoPlayerObserver();
    document.addEventListener("videoPlayerAdded", trackVideoPlayer as EventListener);

    let lastSwipeEventTime: number;
    let lastSwipeStartTime: number;
    let lastSwipeEndTime: number;

    let historyPoppedProgrammatically: boolean = false;
    let jumpingBetweenPopups: boolean = false;

    // Close popup on a history pop-state event (back button pressed)
    const POPSTATE_LISTENER = (e: PopStateEvent) => closePopup();

    // Listen for swipe events and move the popup accordingly if it's open
    const POPUP_SWIPE_LISTENER = (e: SwipeEvent) => {
        if (e.detail.initial) {
            lastSwipeStartTime = Date.now();
            toggleStyleForId("closeX", "on", true); // highlight close X when swiping/scrolling
        }

        // We're publishing swipe events at roughly screen refresh rate, which could be as a high as 120fps. Here we'll
        // check if we've waited the desired interval since the last time we updated the popup's position. If not, we'll
        // short-circuit and try again next time——unless this is a terminating event, in which case we'll process anyway.
        const ongoing: boolean = e.detail.ongoing;
        const millisSinceLastPublish: number = e.detail.eventTime - lastSwipeEventTime;
        if (ongoing && millisSinceLastPublish < SWIPE_SUBSCRIBE_FREQUENCY) {
            return;
        }

        lastSwipeEventTime = e.detail.eventTime;

        if (ongoing) {
            const dir: string = e.detail.cardinal4;

            if (dir === "W" || dir === "E") {
                const oldLeft : number      = parseInt(getPopup().style.left);
                const width   : number      = getPopup().offsetWidth; // doesn't have an explicit CSS width

                // Stop scrolling while we're swiping
                getPopup().style.overflowY = "hidden";

                // Stop updating the popup's left value once it's off the screen in either direction
                const shouldUpdate: boolean = dir === "W" ? (oldLeft > -width) : (oldLeft < width);

                if (shouldUpdate) {
                    let newLeft: number;

                    // Using total distance, instead of just X distance, because element traversal distance should feel
                    // like finger distance travelled, even if some of the finger movement was actually vertical.
                    // Overall, this still has to be a left/right swipe——if the user swipes left, but then starts
                    // dragging their finger up the screen, once the Y distance exceeds the X distance, it will become
                    // an up-swipe and we'll stop animating the popup. Using 75% of this distance helps the popup feel
                    // heavier at low accelerations, and feel like it's sticking to the finger more.
                    const distanceFactor: number = e.detail.totalDistance * 0.75;

                    // We need to be able to react to really quick, short swipes; then finger speed impact falls off
                    // quickly as a factor in popup movement. Impact of acceleration must be at least a factor of 1 to
                    // avoid nerfing the distance factor.
                    const speedFactor: number = Math.max(Math.pow(e.detail.latestSpeedX, 4), 1);

                    // We won't let the popup go too far off screen left or right, as distance traveled during the
                    // return animation will directly affect the visual speed of that animation.
                    if (dir === "W") {
                        newLeft = Math.max(-distanceFactor * speedFactor, -2 * width);
                    }
                    else {
                        newLeft = Math.min(distanceFactor * speedFactor, 2 * width);
                    }

                    // If we're at the beginning or end of the timeline, don't let a swipe wildly fling the popup, since
                    // the same popup will just return to screen.
                    const canSwipeBack: boolean = showingTimelineElemWithPrevious();
                    const canSwipeForward: boolean = showingTimelineElemWithNext();

                    if (dir === "W" && !canSwipeForward) {
                        newLeft = Math.max(newLeft, -10);
                    }
                    else if (dir === "E" && !canSwipeBack) {
                        newLeft = Math.min(newLeft, 10);
                    }

                    let rotation: number;
                    if (dir === "W") {
                        rotation = Math.max(newLeft / 5, -MAX_POPUP_ROTATION);
                    }
                    else {
                        rotation = Math.min(newLeft / 5, MAX_POPUP_ROTATION);
                    }

                    // Set new position and rotation values for the popup
                    getPopup().style.left = newLeft + "px";
                    rotatePopup(rotation);

                    // Show swipe indicators while swiping
                    setUserIsSwiping(true);

                    // Your finger is at about the 60% demarcation horizontally on a mobile screen when the popup has
                    // disappeared off screen, even at the slowest speeds. We'll interpret crossing this threshold as a
                    // desire to navigate back/forward in the content.
                    const thresholdToJump: number = width * 0.6;

                    if (SwipeEvents.telemetryLoggingEnabled()) {
                        const padding: string = newLeft < 0 ? " " : "";
                        const negation: string = newLeft < 0 ? "-" : "";
                        console.info(`
                            Popup position updated by swipe gesture
                            \tDistance factor:           ${padding + distanceFactor.toFixed(2)}
                            \tSpeed factor:              ${padding + speedFactor.toFixed(2)}
                            \tNew popup rotation value:  ${rotation.toFixed(2)}°
                            \tNew popup left value:      ${newLeft.toFixed(2)}px
                            \tThreshold to jump:         ${negation + thresholdToJump.toFixed(2)}px
                            `.replace(/\n[ ]+/g, "\n")
                        );
                    }

                    if (newLeft < -thresholdToJump) {
                        // If you swiped left, you should see the next content element, coming from the right
                        jumpToNext();
                    }
                    else if (newLeft > thresholdToJump) {
                        // If you swiped right, you should see the previous content element, coming from the left
                        jumpToPrevious();
                    }
                }
            }
        }
        else {
            lastSwipeEndTime = Date.now();
            setUserIsSwiping(false);
            toggleStyleForId("closeX", "on", false);

            // If jumping between different popup content, we'll be teleporting the popup to the other side of the
            // screen and don't want to prematurely recall it to origin. Jump logic will take care of getting the popup
            // back to origin when the time is right.
            if (!jumpingBetweenPopups) {
                // Wait the duration of the popup's CSS left transition before returning it to where it started; without
                // this delay, the popup instantly returns to its starting position as soon as you take your finger off
                // the screen, even if you flung it really hard.
                window.setTimeout(() => returnPopupToOrigin(), 500);
            }
        }
    };


    window.addEventListener("DOMContentLoaded", e => {
        browserDetect();
        bindPopupCloseToEsc();
    });


    function bindPopupCloseToEsc() {
        window.addEventListener("keyup", function (e) {
            if (popupIsOpen() && e.keyCode == 27) {
                closePopup();
            }
        });
    }

    // Reinstate content if a popup name is found in the url's hash or in session storage
    export function openPopupIfPopupState(): void {
        if (locationIsHashPath()) {                               // If page loads with a popup hash param, then...
            sessionStorage.setItem(HASH_PATH_KEY, hashPath());    // store the requested popup name
            console.debug(`Found path %c${sessionStorage.getItem(HASH_PATH_KEY)}%c in url; storing and reloading...`,
                "color: blue", "color: unset");
            reloadWithoutHash();                                  // reload w/o popup name so we have a clean back state
        }
        else if (sessionStorage.getItem(HASH_PATH_KEY)) {         // If the page loads with a stored popup name, then...
            const storedPath: string = sessionStorage.getItem(HASH_PATH_KEY)!;  // retrieve the stored name
            sessionStorage.removeItem(HASH_PATH_KEY);             // remove stored name so it doesn't trigger later
            console.debug(`Found path %c${storedPath}%c in session storage; navigating to this content...`,
                "color: blue", "color: unset");
            openPopup(storedPath.substring(1));                   // trim "#" and navigate to corresponding content
        }
    }

    export function openPopup(path: string): void {
        retrieveContent(path, content => {
            clearChildren(getSwipeIndicators());

            if (getTimelinePaths().includes(path)) {
                setupMobileNav(path);

                // Fake a swipe to show the swipe indicators when popup is first opened
                lastSwipeStartTime = Date.now();
                lastSwipeEndTime = lastSwipeStartTime + 1; // needs to be after start
            }

            window.addEventListener("popstate", POPSTATE_LISTENER); // listener for (mobile) back button
            document.addEventListener("swipe", POPUP_SWIPE_LISTENER as EventListener);

            showContentInPopup(path, content);

            window.setTimeout(() => {
                toggleStyleForId("swipe-indicators", "display", true);
                setUserIsSwiping(true);
            }, 300); // wait till popup is fully open to flash swipe indicators
        });
    }

    export function showResume(): void {
        if (window.innerWidth < 800) {
            const download: boolean = confirm("Download my resume?");
            if (download) {
                window.open("/portfolio/assets/documents/Eric-Eldard-Resume.pdf");
            }
        }
        else {
            openPopup("resume");
        }
    }

    function retrieveContent(
        path: string,
        successCallback: ((content: string) => void),
        failureCallback: ((error: Error) => void) = (error => console.error(error.message)),
    ): void {
        // Remove trailing slash if present, then always add it back (supports slash and no-slash paths)
        const basePath: string = window.location.pathname.replace(/\/+$/, "") + "/content/";

        fetch(basePath + path, {redirect: "manual"})
            .then(response => {
                // No access to the location response header here, but the only
                // redirect the app issues for a GET /content request is to login
                if (response.status === 401 || response.status === 403 || response.type === "opaqueredirect") {
                    window.location.assign("/login");
                    return;
                }
                else if (!response.ok) {
                    if (response.status === 502 || response.status === 503) {
                        alert("It looks like we're offline for maintenance.\n\nPlease try back shortly!");
                    }
                    throw new Error(`HTTP ${response.status} response returned for [${basePath}${path}]`);
                }
                return response.text();
            })
            .then(content => {
                if (!content) {
                    throw new Error(`Got null content back for path [${path}]`);
                }
                successCallback(content);
            })
            .catch(error => failureCallback(error));
    }

    // Show the content popup in a freshly opened or transitioned popup
    function showContentInPopup(path: string, content: string): void {
        const main         : HTMLElement        = document.getElementById("main")!;
        const container    : HTMLElement        = document.getElementById("popup-container")!;
        const popupContent : HTMLElement        = document.getElementById("popup-content")!;
        const timelineElem : HTMLElement | null = getTimelineElem(path);
        const hashPath     : string             = `#${path}`;

        // If a timeline event kicked this method off, highlight that event
        if (timelineElem) {
            timelineElem.classList.add("focused");
        }

        setInnerHTML(popupContent, content);
        determineDesktopNavVisibility(path);
        setMobileNavPath(path);

        // Tracking the path for the open popup solely for logging purposes when page is refreshed while popup is open
        setDataName(getPopup(), hashPath);

        // Changing effects behind the popup
        main.classList.add("blur");
        container.classList.add("open");
        setBodyBackgroundColor(path);

        window.setTimeout(() => {
            if (!popupIsOpen()) {
                setPopupIsOpen(true);
            }

            // Push an extra frame onto history, so the back button can be
            // used to close the popup without navigating away from the page
            history.pushState({ popupStatus : "open"}, "", hashPath);
            console.debug(`Popup %c${hashPath}%c opened and added to history`, "color: blue", "color: unset");

            // Now that new content is loaded and set in the popup, recall it to the center of the screen
            getPopup().scrollTop = 0;
            returnPopupToOrigin();

            jumpingBetweenPopups = false;
            historyPoppedProgrammatically = false;
        }, 100); // Buffer time for background animations

        setUserIsSwiping(false);
    }

    // Fully close the popup and return to the timeline
    export function closePopup(): void {
        // History is popped programmatically when transitioning between popup cards; it triggers the popstate listener,
        // which calls this close method, but we'll ignore it when we know it came from a transition vs the back button.
        if (historyPoppedProgrammatically) {
            historyPoppedProgrammatically = false;
            return;
        }

        window.removeEventListener("popstate", POPSTATE_LISTENER);
        document.removeEventListener("swipe", POPUP_SWIPE_LISTENER as EventListener);

        toggleStyleForId("swipe-indicators", "display", false); // hide immediately to prevent fade-out animation
        setPopupIsOpen(false);

        transitionPopup();
    }

    // A partial close, for jumping from one popup card to another (mostly animated stuff)
    function transitionPopup(): void {
        const main                  : HTMLElement = document.getElementById("main")!;
        const container             : HTMLElement = document.getElementById("popup-container")!;
        const popupContent          : HTMLElement = document.getElementById("popup-content")!;
        const locationWasHashParam  : boolean     = locationIsHashPath();

        // If the popup's hash param is currently in browser history, we'll pop it off. It may not be, if the hash param
        // url was directly navigated to. I've attempted to push an event into history in this case, but Chrome & Safari
        // don't recognize it (though Firefox does), so a history.back() op would cause the user to leave the site.
        if (locationIsHashPath()) {
            historyPoppedProgrammatically = true;
            history.back();
        }

        // We have to retrieve the current popup's name from the popup element, because at this point the hash param has
        // been removed from the address and the popstate event doesn't contain info about the popped-state (it points
        // to the new history head)
        console.debug(`Popup %c${getDataName(getPopup())}%c removed from history`, "color: blue", "color: unset");

        document.body.classList.remove("opaque-bg-color");
        main.classList.remove("blur");

        toggleStyleForId("closeX", "on", false)

        clearDataName(getPopup());

        videoPlayerObserver.disconnect();
        Video.destroyAllPlayers();

        console.debug(`Popup closed${locationWasHashParam ? "" : " with back button"}`);

        // Finish CSS animations before hiding
        window.setTimeout(() => {
            container.classList.remove("open");
            setInnerHTML(popupContent, "");

            window.setTimeout(() =>
                Array.from(document.querySelectorAll(".timeline .timeline-events .timeline-event"))
                    .forEach(elem => elem.classList.remove("focused"))
            , 700);
        }, 300);
    }

    function returnPopupToOrigin() {
        rotatePopup(0);
        getPopup().style.left = "0";
        getPopup().style.overflowY = "scroll";
    }

    // Jump straight from one content popup to another. "comingFrom" specifies where the new popup should enter the
    // screen from and values are "left", "center" (or null), and "right"
    function jumpTo(path: string, comingFrom: string = "center"): void {
        jumpingBetweenPopups = true;
        transitionPopup();

        window.setTimeout(() => {
            // Teleport the popup to its new starting position
            if (comingFrom === "left") {
                getPopup().style.left = -window.screen.availWidth + "px";
                rotatePopup(-MAX_POPUP_ROTATION);
            }
            else if (comingFrom === "right") {
                getPopup().style.left = window.screen.availWidth + "px";
                rotatePopup(MAX_POPUP_ROTATION);
            }

            // Show the new content
            retrieveContent(path, content => showContentInPopup(path, content));
        }, 500); // wait for prior swipe animation to complete
    }

    // Jump to the content popup that comes sequentially before the open one (if present)
    export function jumpToPrevious(): void {
        // Remove focus from any active timeline element
        const elem: HTMLElement | null = getCurrentTimelineElem();
        if (elem) {
            elem.classList.remove("focused");
        }

        // Fling the current popup off screen
        getPopup().style.left = window.screen.availWidth + "px";
        rotatePopup(MAX_POPUP_ROTATION);

        // If there's content before the current popup, show it
        const previousSibling: HTMLElement = elem?.previousElementSibling as HTMLElement;
        if (previousSibling) {
            previousSibling.classList.add("focused");
            jumpTo(previousSibling.dataset.timelinePath!, "left");
        }
    }

    // Jump to the content popup that comes sequentially after the open one (if present)
    export function jumpToNext(): void {
        // Remove focus from any active timeline element
        const elem: HTMLElement | null = getCurrentTimelineElem();
        if (elem) {
            elem.classList.remove("focused");
        }

        // Fling the current popup off screen
        getPopup().style.left = -window.screen.availWidth + "px";
        rotatePopup(-MAX_POPUP_ROTATION);

        // If there's content after the current popup, show it
        const nextSibling: HTMLElement = elem?.nextElementSibling as HTMLElement;
        if (nextSibling) {
            nextSibling.classList.add("focused");
            jumpTo(nextSibling.dataset.timelinePath!, "right");
        }
    }

    // Change the background color of the body to match the given path, if that path matches a timeline element
    function setBodyBackgroundColor(path: string): void {
        // Previous color not removed in transitionPopup() because it needs time to fade from opaque to transparent
        removeClassesStartingWith(document.body, "timeline-color-");

        const elem: HTMLElement | null = getTimelineElem(path);

        if (elem) {
            const position: number = Array.prototype.indexOf.call(elem.parentNode?.children, elem);
            document.body.classList.add(`timeline-color-${position}`);
            document.body.classList.add("opaque-bg-color");
        }
    }

    // Desktop only
    // Only show the back and forward arrows if the content is for a timeline event. If it is, only show back if there's
    // previous content on the timeline and only show forward if there's more content.
    function determineDesktopNavVisibility(path: string): void {
        const elem: HTMLElement | null = getTimelineElem(path);
        if (elem) {
            document.getElementById("move-back")!.style.visibility = elem.previousElementSibling ? "visible" : "hidden";
            document.getElementById("move-forward")!.style.visibility = elem.nextElementSibling ? "visible" : "hidden";
        }
        else {
            document.getElementById("move-back")!.style.visibility = "hidden";
            document.getElementById("move-forward")!.style.visibility = "hidden";
        }
    }

    // If the popup is showing for any timeline element, add swipe indicator dots
    function setupMobileNav(path: string, recreate: boolean = false): void {
        getTimelinePaths().forEach(timelinePath => {
            const dotDiv: HTMLElement = document.createElement("div");
            dotDiv.classList.add("swipe-dot");
            getSwipeIndicators().appendChild(dotDiv);
        });
    }

    // If the popup is showing for a timeline element, highlight the corresponding swipe indicator dot
    function setMobileNavPath(path: string): void {
        const timelinePaths: string[] = getTimelinePaths();

        if (timelinePaths.includes(path)) {
            for (let i = 0; i < getSwipeIndicators().children.length; i++) {
                toggleStyle(getSwipeIndicators().children[i], "embiggen", timelinePaths[i] == path)
            }
        }
    }

    function getTimelinePaths(): string[] {
        return Array.from(document.querySelectorAll("#main .content .timeline-events .timeline-event"))
            .map(elem => elem as HTMLElement)
            .map(htmlElem => htmlElem.dataset.timelinePath || '');
    }

    function showingTimelineElemWithPrevious(): boolean {
        return getCurrentTimelineElem()?.previousElementSibling !== null;
    }

    function showingTimelineElemWithNext(): boolean {
        return getCurrentTimelineElem()?.nextElementSibling !== null;
    }

    function getTimelineElem(path: string): HTMLElement | null {
        if (path && path !== "") {
            return document.querySelectorAll(`[data-timeline-path=${path}]`)[0] as HTMLElement;
        }
        return null;
    }

    function getCurrentTimelineElem(): HTMLElement | null {
        const path: string = hashPath().substring(1);
        if (path && path !== "") {
            return getTimelineElem(path);
        }
        return null;
    }

    function getPopup(): HTMLElement {
        return document.getElementById("popup")!;
    }

    function popupIsOpen(): boolean {
        return getPopup().classList.contains("open");
    }
    
    function setPopupIsOpen(isOpen: boolean): void {
        toggleStyle(getPopup(), "open", isOpen);
    }

    function rotatePopup(deg: number): void {
        getPopup().style.transform = `rotate(${deg}deg) translateY(-50%)`;
    }

    function getSwipeIndicators(): HTMLElement {
        return document.getElementById("swipe-indicators")!;
    }

    function userIsSwiping(): boolean {
        return getSwipeIndicators().classList.contains("on");
    }

    function setUserIsSwiping(swiping: boolean): void {
        if (swiping) {
            if (!userIsSwiping()) { // don't reset animation if already playing
                toggleStyleForId("swipe-indicators", "on", true);
                toggleStyleForId("swipe-indicators", "off", false);
            }
        }
        else {
            const lastSwipeEndAtCallTime = lastSwipeEndTime;
            window.setTimeout(() => {
                // if another swipe-start or swipe-end has occurred, ignore this one
                if (lastSwipeEndAtCallTime == lastSwipeEndTime && lastSwipeEndTime > lastSwipeStartTime) {
                    toggleStyleForId("swipe-indicators", "on", false);
                    toggleStyleForId("swipe-indicators", "off", true);
                }
            }, 2500);
        }
    }

    function createVideoPlayerObserver(): IntersectionObserver {
        const observerOptions = {
            root: getPopup(),
            rootMargin: "0px",
            threshold: 0,
        };

        const pauseOffscreenPlayers = (entries: IntersectionObserverEntry[]) => {
            entries.forEach((entry: IntersectionObserverEntry) => {
                const playerId = entry.target.id;
                const playerOffscreen: boolean = !entry.isIntersecting;
                console.debug(
                    `videoPlayerObserver: %c${playerId}%c is ${playerOffscreen ? "offscreen" : "onscreen"}`,
                    "font-style: italic", "font-style: unset"
                );
                if (playerOffscreen) {
                    Video.pausePlayer(playerId);
                }
            });
        };

        return new IntersectionObserver(pauseOffscreenPlayers, observerOptions);
    }

    function trackVideoPlayer(playerAddedEvent: Video.PlayerAddedEvent) {
        const iframe: HTMLIFrameElement = playerAddedEvent.detail;
        window.setTimeout(() => {
            videoPlayerObserver.observe(iframe);
            console.debug(`videoPlayerObserver is tracking %c${iframe.id}`, "font-style: italic");
        }, 500); // delay tracking long enough for element to render; avoids initial false-positive "offscreen" message
    }

    export function togglePasswordVisibility(container: HTMLElement): void {
        const inputElem: HTMLInputElement = document.querySelector(`#${container} input`)!;
        inputElem.type = inputElem.type === "password" ? "text" : "password";

        const showElem: HTMLElement = document.querySelector(`#${container} .visibility-toggle .password-show`)!;
        const hideElem: HTMLElement = document.querySelector(`#${container} .visibility-toggle .password-hide`)!;

        if (showElem.style.display === "none") {
            showElem.style.display = "unset";
            hideElem.style.display = "none";
        }
        else {
            showElem.style.display = "none";
            hideElem.style.display = "unset";
        }
    }

    export function cycleFontColors(
            containerId: string,
            rBegin: number, gBegin: number, bBegin: number,
            rEnd: number,   gEnd: number,   bEnd: number
        ): void {
        const container: HTMLElement = document.getElementById(containerId)!;
        const text: string = container.innerText;
        const rIncrement: number = (rEnd - rBegin) / (text.length - 1);
        const gIncrement: number = (gEnd - gBegin) / (text.length - 1);
        const bIncrement: number = (bEnd - bBegin) / (text.length - 1);
        let outputHTML: string = "";
        for (let i = 0; i < text.length; i++) {
            const red   : number = rBegin + Math.round(rIncrement * i);
            const green : number = gBegin + Math.round(gIncrement * i);
            const blue  : number = bBegin + Math.round(bIncrement * i);
            outputHTML += `<span style="color: rgb(${red}, ${green}, ${blue})">${text.charAt(i)}</span>`;
        }
        container.innerHTML = outputHTML;
    }
}