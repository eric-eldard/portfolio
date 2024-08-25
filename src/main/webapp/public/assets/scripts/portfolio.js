const HASH_PATH_KEY = "hashPath";
const POPSTATE_LISTENER = (event) => closePopup();

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
    // Remove trailing slash if present, then always add it back (supports slash and no-slash paths)
    const basePath = window.location.pathname.replace(/\/+$/, "") + "/content/";

    fetch(basePath + path)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP ${response.status} response returned for [${basePath + path}]`);
            }
            return response.text();
        })
        .then(text => showContentInPopup(text, path))
        .catch(error => console.error(error.message));
}

//Show the content pop-up and populate w/ content
function showContentInPopup(content, path) {
    const main       = document.getElementById("main");
    const background = document.getElementById("popup-background");
    const popup      = document.getElementById("popup");
    const hashPath   = `#${path}`;

    const innerHTML = `
        <div id="closeX">
            <a href="javascript: closePopup();" title="Close">&#x2715;</a>
        </div>
        ${content}
    `;

    setInnerHTML(popup, innerHTML);
    setDataName(popup, hashPath)

    // TODO ERIC position=fixed causing resizing of main content when popup open
    // main.style.overflow = "hidden";
    // main.style.position = "fixed";

    background.classList.add("open");

    setTimeout(function() {
        popup.classList.add("open");
        popup.scrollTop = 0;

        // Add listener for (mobile) back button, and push an extra frame onto history, so the
        // back button can be used to close the popup without navigating away from the page
        history.pushState({ popupStatus : "open"}, "", hashPath);
        window.addEventListener("popstate", POPSTATE_LISTENER);
        console.debug(`Popup %c${hashPath}%c opened and added to history`, "color: blue", "color: unset");
    }, 100);
}

function closePopup() {
    const main        = document.getElementById("main");
    const background  = document.getElementById("popup-background");
    const popup       = document.getElementById("popup");
    const wasPopState = isPopupState();

    window.removeEventListener("popstate", POPSTATE_LISTENER);
    if (isPopupState()) {
        history.back();
    }

    // Must retrieve popup name from the popup element, because at this point the hash param has been removed from the
    // address and the popstate event doesn't contain info about the popped-state (it points to the new history head)
    console.debug(`Popup %c${getDataName(popup)}%c removed from history`, "color: blue", "color: unset");

    popup.classList.remove("open");
    clearDataName(popup);

    // TODO ERIC position=fixed causing resizing of main content when popup open
    // main.style.overflow = "unset";
    // main.style.position = "unset";

    video.destroyAllPlayers();

    console.debug(`Popup closed${wasPopState ? "" : " with back button"}`);

    setTimeout(function() {
        background.classList.remove("open");
        popup.innerHTML = "";
    }, 300);
}

//Jump straight from one content window to another
function jumpTo(path) {
    closePopup();
    setTimeout(function(){ retrieveAndShowContent(path); }, 501);
}

// See https://stackoverflow.com/questions/2592092/executing-script-elements-inserted-with-innerhtml
function setInnerHTML(container, html) {
    container.innerHTML = html;

    Array.from(container.querySelectorAll("script"))
        .forEach(oldScriptElem => {
            const newScriptElem = document.createElement("script");

            Array.from(oldScriptElem.attributes).forEach(attr => {
                newScriptElem.setAttribute(attr.name, attr.value)
            });

        const scriptText = document.createTextNode(oldScriptElem.innerHTML);
        newScriptElem.appendChild(scriptText);

        oldScriptElem.parentNode.replaceChild(newScriptElem, oldScriptElem);
    });
}

function getMetaValue(name) {
    return document.querySelector(`meta[name="${name}"`).getAttribute('content');
}

function makeRequestOptions(method) {
    return makeRequestOptions(method, null);
}

function makeRequestOptions(method, body) {
    const hasBody = typeof body !== 'undefined';
    return {
        method: method,
        headers: {
            ...(hasBody ? {"Content-Type": "application/json"} : {}),
            "X-CSRF-TOKEN": getMetaValue("_csrf")
        },
        ...(hasBody ? (hasBody ? {body: JSON.stringify(body)} : {body: body}) : {})
    };
}

function getDataName(elem) {
    return elem.dataset.name;
}

function setDataName(elem, name) {
    elem.dataset.name = name;
}

function clearDataName(elem) {
    setDataName(elem, "");
}

function hashPath() {
    return window.location.hash;
}

// Returns true if hash path is not empty and not just "#"
function isPopupState() {
    return hashPath().length > 1;
}

function reloadWithoutHash() {
    window.location.replace(window.location.href.replace(window.location.hash, ""));
}

function setFrameSrc(frameElemId, src) {
    document.getElementById(frameElemId).contentWindow.location.replace(src);
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