const SWIPE_SUBSCRIBE_FREQUENCY = 1000 / 120; // 120fps necessary for smooth (non-jittery) animation on iOS Safari
const MAX_POPUP_ROTATION = 30;
const HASH_PATH_KEY = "hashPath";
const POPSTATE_LISTENER = (e) => closePopup(); // Close popup on a history pop-state event (back button pressed)

// Listen for swipe events and move the popup accordingly if it's open
let lastSwipeEventTime = 0;
const POPUP_SWIPE_LISTENER = e => {

    // We're publishing swipe events at roughly screen refresh rate, which could be as a high as 120fps. Here we'll
    // check if we've waited the desired interval since the last time we updated the popup's position. If not, we'll
    // short-circuit and try again next time——unless this is a terminating event, in which case we'll process anyway.
    const ongoing = e.detail.ongoing;
    const millisSinceLastPublish = e.detail.eventTime - lastSwipeEventTime;
    if (ongoing && millisSinceLastPublish < SWIPE_SUBSCRIBE_FREQUENCY) {
        return;
    }

    lastSwipeEventTime = e.detail.eventTime;

    if (ongoing) {
        const dir = e.detail.cardinal4dir;

        if (dir === "W" || dir === "E") {
            const popup   = getPopup();
            const oldLeft = popup.style.left.replace("px", "");
            const width   = popup.offsetWidth; // doesn't have an explicit CSS width

            // Stop scrolling while we're swiping
            popup.style.overflowY = "hidden";

            // Stop updating the popup's left value once it's off the screen in either direction
            const shouldUpdate = dir === "W" ? (oldLeft > -width) : (oldLeft < width);

            if (shouldUpdate) {
                let newLeft;

                // Using total distance, instead of just X distance, because element traversal distance should feel like
                // finger distance travelled, even if some of the finger movement was actually vertical. Overall, this
                // still has to be a left/right swipe——if the user swipes left, but then starts dragging their finger up
                // the screen, once the Y distance exceeds the X distance, it will become an up-swipe and we'll stop
                // animating the popup. Using 75% of this distance helps the popup feel heavier at low accelerations,
                // and feel like it's sticking to the finger more.
                const distanceFactor = e.detail.totalDistance * 0.75;

                // We need to be able to react to really quick, short swipes, then finger speed impact falls off quickly
                // as a factor in movement of the popup. Impact of acceleration must be at least 1 to avoid nerfing the
                // distance factor.
                const speedFactor = Math.max(Math.pow(e.detail.latestSpeedX, 4), 1);

                // We won't let the popup go too far off screen left or right, as distance traveled during the return
                // animation will directly affect the visual speed of that animation.
                if (dir === "W") {
                    newLeft = Math.max(-distanceFactor * speedFactor, -2 * width);
                }
                else {
                    newLeft = Math.min(distanceFactor * speedFactor, 2 * width);
                }

                // If we're at the beginning or end of the timeline, don't let a swipe wildly fling the popup, since
                // the same popup will just return to screen.
                const canSwipeBack = showingTimelineElemWithPrevious();
                const canSwipeForward = showingTimelineElemWithNext();

                if (dir === "E" && !canSwipeBack) {
                    newLeft = Math.min(newLeft, 10);
                }
                else if (dir === "W" && !canSwipeForward) {
                    newLeft = Math.max(newLeft, -10);
                }

                // Set new position and rotation values for the popup
                popup.style.left = newLeft + "px";
                const rotation = Math.min(newLeft / 5, (dir === "W" ? -MAX_POPUP_ROTATION : MAX_POPUP_ROTATION));
                rotatePopup(newLeft / 5);

                // Show swipe indicators while swiping
                toggleStyle("swipe-indicators", "pause", true); // if the indicator flash is still playing, pause it
                toggleStyle("swipe-indicators", "sustain-swipe", true);

                // Your finger is at about the 60% demarcation horizontally on a mobile screen when the popup has
                // disappeared off screen, even at the slowest speeds. We'll interpret crossing this threshold as a
                // desire to navigate back/forward in the content.
                const thresholdToJump = width * 0.6;

                if (SwipeEvents.telemetryLoggingEnabled()) {
                    const padding = newLeft < 0 ? " " : "";
                    console.info(`
                        Popup position updated by swipe gesture
                        \tDistance factor:       ${padding + distanceFactor}
                        \tSpeed factor:          ${padding + speedFactor}
                        \tNew popup left value:  ${popup.style.left}
                        \tThreshold to jump:      (-/+)${thresholdToJump}
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
        // Hide swipe indicators
        toggleStyle("swipe-indicators", "sustain-swipe", false);
        toggleStyle("swipe-indicators", "pause", false);

        // Wait the duration of the popup's CSS left transition before returning it to where it started; without this
        // delay, the popup returns to its starting position as soon as you take your finger off the screen, even if
        // you flung it really hard.
        window.setTimeout(() => {
            popup.style.left = "0";
            rotatePopup(0);
            popup.style.overflowY = "scroll";
        }, 501);
    }
};


window.addEventListener("DOMContentLoaded", e => {
    browserDetect();
    bindPopupCloseToEsc();
});


function bindPopupCloseToEsc() {
    window.addEventListener("keyup", function (e) {
        if (popupIsOpen() && event.keyCode == 27) {
            closePopup();
        }
    });
}

// Reinstate content if a popup name is found in the url's hash or in session storage
function retrieveAndShowContentIfPopupState() {
    if (isPopupState()) {                                         // If page loads with a popup hash param, then...
        sessionStorage.setItem(HASH_PATH_KEY, hashPath());        // store the requested popup name
        console.debug(`Found path %c${sessionStorage.getItem(HASH_PATH_KEY)}%c in url; storing and reloading...`,
            "color: blue", "color: unset");
        reloadWithoutHash();                                      // reload w/o popup name so we have a clean back state
    }
    else if (sessionStorage.getItem(HASH_PATH_KEY)  !== null) {   // If the page loads with a stored popup name, then...
        const storedPath = sessionStorage.getItem(HASH_PATH_KEY); // retrieve the stored name
        sessionStorage.removeItem(HASH_PATH_KEY);                 // remove stored name so it doesn't trigger later
        console.debug(`Found path %c${storedPath}%c in session storage; navigating to this content...`,
            "color: blue", "color: unset");
        retrieveAndShowContent(storedPath.substring(1));          // trim "#" and navigate to corresponding content
    }
}

// Retrieve content and display it in the popup
function retrieveAndShowContent(path) {

    // If a timeline event kicked this method off, highlight that event
    const elem = getTimelineElem(path);
    if (elem) {
        elem.classList.add("focused");
    }

    // Remove trailing slash if present, then always add it back (supports slash and no-slash paths)
    const basePath = window.location.pathname.replace(/\/+$/, "") + "/content/";

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
        .then(text => showContentInPopup(text, path))
        .catch(error => console.error(error.message));
}

//Show the content pop-up and populate w/ content
function showContentInPopup(content, path) {
    const main         = document.getElementById("main");
    const background   = document.getElementById("popup-background");
    const popup        = getPopup();
    const popupContent = document.getElementById("popup-content");
    const hashPath     = `#${path}`;

    setInnerHTML(popupContent, content);
    determineNavigationVisibility(path);
    setTimelineElemSwipeDots(path);

    // Tracking the path for the open popup solely for logging purposes when page is refreshed while popup is open
    setDataName(popup, hashPath);

    // Changing effects behind the popup
    background.classList.add("open");
    main.classList.add("blur");
    setBodyBackgroundColor(path);

    document.addEventListener("swipe", POPUP_SWIPE_LISTENER);

    window.setTimeout(() => {
        popup.classList.add("open");
        popup.style.overflowY = "scroll"; // we may have frozen this for swiping; let's make double sure it's unfrozen
        popup.scrollTop = 0;

        // Add listener for (mobile) back button, and push an extra frame onto history, so the
        // back button can be used to close the popup without navigating away from the page
        history.pushState({ popupStatus : "open"}, "", hashPath);
        window.addEventListener("popstate", POPSTATE_LISTENER);
        console.debug(`Popup %c${hashPath}%c opened and added to history`, "color: blue", "color: unset");

        window.setTimeout(() => {
            // Give a little buffer for loading before calling the popup back to center screen
            rotatePopup(0);
            popup.style.left = "0";
            toggleStyle("swipe-indicators", "flash-swipe", true);
        }, 400);
    }, 100);
}

function closePopup() {
    const main         = document.getElementById("main");
    const background   = document.getElementById("popup-background");
    const popup        = getPopup();
    const popupContent = document.getElementById("popup-content");
    const wasPopState  = isPopupState();

    window.removeEventListener("popstate", POPSTATE_LISTENER);
    document.removeEventListener("swipe", POPUP_SWIPE_LISTENER);

    // If the popup's hash param is currently in browser history, we'll pop it off. It may not be, if the hash param url
    // was directly navigated to. I've attempted to push an event into history in this case, but Chrome and Safari don't
    // recognize it (though Firefox does), so a history.back() op would cause the user to leave the site.
    if (isPopupState()) {
        history.back();
    }

    // Must retrieve popup name from the popup element, because at this point the hash param has been removed from the
    // address and the popstate event doesn't contain info about the popped-state (it points to the new history head)
    console.debug(`Popup %c${getDataName(popup)}%c removed from history`, "color: blue", "color: unset");

    document.body.classList.remove("opaque-bg-color");
    main.classList.remove("blur");

    // Perform fade-out animation if we're closing the popup entirely, but not if we're swiping between content
    if (popup.classList.contains("swiping")) {
        popup.classList.remove("swiping");
    }
    else {
        popup.classList.remove("open");
    }

    // Hide all swipe indicators
    toggleStyle("swipe-indicators", "pause", false);
    toggleStyle("swipe-indicators", "flash-swipe", false);
    toggleStyle("swipe-indicators", "sustain-swipe", false);


    clearDataName(popup);

    video.destroyAllPlayers();

    console.debug(`Popup closed${wasPopState ? "" : " with back button"}`);

    // Finish CSS animations before completely hiding
    window.setTimeout(() => {
        background.classList.remove("open");
        setInnerHTML(popupContent, "");

        window.setTimeout(() =>
            Array.from(document.querySelectorAll(".timeline .timeline-events .timeline-event"))
                .forEach(elem => elem.classList.remove("focused"))
        , 700);

    }, 300);
}

function showResume() {
    if (window.innerWidth < 800) {
        alert('TODO ERIC - updated resume');
    }
    else {
        retrieveAndShowContent('resume');
    }
}

// Jump straight from one content popup to another. "comingFrom" specifies where the new popup should enter the screen
// from and values are "left", "center" (or null), and "right"
function jumpTo(path, comingFrom = "center") {
    const popup = getPopup();
    popup.classList.add("swiping");

    closePopup();

    window.setTimeout(() => {
        // Push the popup to it's new starting position
        if (comingFrom === "left") {
            popup.style.left = -window.screen.availWidth + "px";
            rotatePopup(-MAX_POPUP_ROTATION);
        }
        else if (comingFrom === "right") {
            popup.style.left = window.screen.availWidth + "px";
            rotatePopup(MAX_POPUP_ROTATION);
        }

        // Show the new content
        retrieveAndShowContent(path);
    }, 501);
}

// Jump to the content popup that comes sequentially before the open one (if present)
function jumpToPrevious() {
    // Remove focus from any active timeline element
    const elem = getCurrentTimelineElem();
    if (elem) {
        elem.classList.remove("focused");
    }

    // Fling the current popup off screen
    getPopup().style.left = window.screen.availWidth + "px";
    rotatePopup(MAX_POPUP_ROTATION);

    // If there's content before the current popup, show it
    if (elem && elem.previousElementSibling) {
        elem.previousElementSibling.classList.add("focused");
        jumpTo(elem.previousElementSibling.dataset.timelinePath, "left");
    }
}

// Jump to the content popup that comes sequentially after the open one (if present)
function jumpToNext() {
    // Remove focus from any active timeline element
    const elem = getCurrentTimelineElem();
    if (elem) {
        elem.classList.remove("focused");
    }

    // Fling the current popup off screen
    getPopup().style.left = -window.screen.availWidth + "px";
    rotatePopup(-MAX_POPUP_ROTATION);

    // If there's content after the current popup, show it
    if (elem && elem.nextElementSibling) {
        elem.nextElementSibling.classList.add("focused");
        jumpTo(elem.nextElementSibling.dataset.timelinePath, "right");
    }
}

// Change the background color of the body to match the given path, if that path matches a timeline element
function setBodyBackgroundColor(path) {
    // Previous color not removed in closePopup() because it needs time to fade from opaque to transparent
    removeClassesStartingWith(document.body, "timeline-color-");

    const elem = getTimelineElem(path);

    if (elem) {
        const position = Array.prototype.indexOf.call(elem.parentNode.children, elem);
        document.body.classList.add(`timeline-color-${position}`);
        document.body.classList.add("opaque-bg-color");
    }
}

// Desktop only
// Only show the back and forward arrows if the content is for a timeline event. If it is, only show back if there's
// previous content on the timeline and only show forward if there's more content.
function determineNavigationVisibility(path) {
    const elem = getTimelineElem(path);
    if (elem) {
        document.getElementById("move-back").style.visibility = elem.previousElementSibling ? "visible" : "hidden";
        document.getElementById("move-forward").style.visibility = elem.nextElementSibling ? "visible" : "hidden";
    }
    else {
        document.getElementById("move-back").style.visibility = "hidden";
        document.getElementById("move-forward").style.visibility = "hidden";
    }
}

function getTimelineElem(path) {
    if (path && path !== "") {
        return document.querySelectorAll(`[data-timeline-path=${path}]`)[0];
    }
    return null;
}

function getCurrentTimelineElem() {
    const path = hashPath().substring(1);
    if (path && path !== "") {
        return getTimelineElem(path);
    }
    return null;
}

function setTimelineElemSwipeDots(path) {
    const swipeDotsContainer = document.getElementById("swipe-indicators");
    clearChildren(swipeDotsContainer);

    const timelinePaths = Array.from(document.querySelectorAll("#main .content .timeline-events .timeline-event"))
        .map(elem => elem.dataset.timelinePath);

    // If the popup is showing for a timeline element, add swipe indicator dots
    if (timelinePaths.includes(path)) {
        timelinePaths.forEach(function (timelinePath) {
            const dotDiv = document.createElement("div");
            dotDiv.classList.add("swipe-dot");
            if (timelinePath === path) {
                dotDiv.classList.add("embiggen");
            }
            swipeDotsContainer.appendChild(dotDiv);
        });
    }
}

function showingTimelineElemWithPrevious() {
    const elem = getCurrentTimelineElem();
    return elem && elem.previousElementSibling !== null;
}

function showingTimelineElemWithNext() {
    const elem = getCurrentTimelineElem();
    return elem && elem.nextElementSibling !== null;
}

function getPopup() {
    return document.getElementById("popup");
}

function popupIsOpen() {
    return getPopup().classList.contains("open");
}

function rotatePopup(deg) {
    getPopup().style.transform = `rotate(${deg}deg) translateY(-50%)`;
}

// Returns true if hash path is not empty and not just "#"
function isPopupState() {
    return hashPath().length > 1;
}

function togglePasswordVisibility(container) {
    const inputElem = document.querySelector(`#${container} input`);
    inputElem.type = inputElem.type === "password" ? "text" : "password";

    const showElem = document.querySelector(`#${container} .visibility-toggle .password-show`);
    const hideElem = document.querySelector(`#${container} .visibility-toggle .password-hide`);

    if (showElem.style.display === "none") {
        showElem.style.display = "unset";
        hideElem.style.display = "none";
    }
    else {
        showElem.style.display = "none";
        hideElem.style.display = "unset";
    }
}

function cycleFontColors(containerId, rBegin, gBegin, bBegin, rEnd, gEnd, bEnd) {
    const container = document.getElementById(containerId);
    const text = container.innerText;
    const rIncrement = (rEnd - rBegin) / (text.length - 1);
    const gIncrement = (gEnd - gBegin) / (text.length - 1);
    const bIncrement = (bEnd - bBegin) / (text.length - 1);
    let outputHTML = "";
    for (let i = 0; i < text.length; i++) {
        const red   = rBegin + Math.round(rIncrement * i);
        const green = gBegin + Math.round(gIncrement * i);
        const blue  = bBegin + Math.round(bIncrement * i);
        outputHTML += `<span style="color: rgb(${red}, ${green}, ${blue})">${text.charAt(i)}</span>`;
    }
    container.innerHTML = outputHTML;
}