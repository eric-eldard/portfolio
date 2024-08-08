// Retrieve content and display it in the popup
function retrieveAndShowContent(path) {
    fetch("content/" + path)
      .then(response => response.text())
      .then(text => showPopup(text));
}

//Show the content pop-up and populate w/ content
function showPopup(content) {
    const main   = document.getElementById("main");
    const dialog = document.getElementById("dialog");
    const popup  = document.getElementById("popup");

    const innerHTML = `
        <div id="closeX">
            <a href="javascript: closePopup(&#39;&#39;);" title="Close">&#x2715;</a>
        </div>
        ${content}
    `;

    setInnerHTML(popup, innerHTML);

    dialog.style.display = "block";
    setTimeout(function() {
        popup.style.width = "80%";
        popup.scrollTop = 0;
    }, 100);
}

function closePopup() {
    popup.style.width = "0px";
    setTimeout(function() {
        dialog.style.display = "none";
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

function getVideoPlayers() {
    return document.getElementsByClassName("embedded-video");
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