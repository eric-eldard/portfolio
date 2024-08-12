const popupOpenParam = "#popup";
const popstateListener = (event) => closePopup();

// Retrieve content and display it in the popup
function retrieveAndShowContent(path) {
    fetch("content/" + path)
      .then(response => response.text())
      .then(text => showPopup(text));
}

//Show the content pop-up and populate w/ content
function showPopup(content) {
    const main       = document.getElementById("main");
    const background = document.getElementById("popup-background");
    const popup      = document.getElementById("popup");

    const innerHTML = `
        <div id="closeX">
            <a href="javascript: closePopup(&#39;&#39;);" title="Close">&#x2715;</a>
        </div>
        ${content}
    `;

    setInnerHTML(popup, innerHTML);

    // TODO ERIC position=fixed causing resizing of main content when popup open
    // main.style.overflow = "hidden";
    // main.style.position = "fixed";

    background.style.display = "block";

    setTimeout(function() {
        popup.style.width = "80%";
        popup.scrollTop = 0;

        // Add listener for (mobile) back button, and push an extra frame onto history, so the
        // back button can be used to close the popup without navigating away from the page
        history.pushState({ popupStatus : "open"}, "", popupOpenParam);
        window.addEventListener("popstate", popstateListener);
    }, 100);
}

function closePopup() {
    const main       = document.getElementById("main");
    const background = document.getElementById("popup-background");
    const popup      = document.getElementById("popup");

    window.removeEventListener("popstate", popstateListener);
    if (isPopupState()) {
        history.back();
    }

    popup.style.width = "0px";

    // TODO ERIC position=fixed causing resizing of main content when popup open
    // main.style.overflow = "unset";
    // main.style.position = "unset";

    video.destroyAllPlayers();

    setTimeout(function() {
        background.style.display = "none";
        popup.innerHTML = "";
    }, 300);
}

//Jump straight from one content window to another
function jumpTo(content) {
    closePopup("");
    setTimeout(function(){ showPopup(content); }, 501);
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
    return document.querySelector("meta[name='" + name + "']").getAttribute('content');
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
        ...(hasBody ? {body: JSON.stringify(body)} : {})
    };
}

function isPopupState() {
    return window.location.hash === popupOpenParam;
}

function ensureNotPopupState() {
    if (isPopupState()) {
        history.pushState("", "", window.location.pathname + window.location.search);
    }
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