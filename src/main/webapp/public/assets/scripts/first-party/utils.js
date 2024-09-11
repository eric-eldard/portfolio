/**
 * Utility functions
 * @version 1.0
 * @author  Eric Eldard
 */


/*******************/
/*  Element Utils  */
/*******************/

/**
 * Gets the value of the <code>data-name</code> attribute for the given element
 * @param {Object} elem - the HTML element
 */
function getDataName(elem) {
    return elem.dataset.name;
}

/**
 * Sets the value of the <code>data-name</code> attribute for the given element
 * @param {Object} elem  - the HTML element
 * @param {string} value - the value to set for the element's <code>data-name</code> attribute
 */
function setDataName(elem, value) {
    elem.dataset.name = value;
}

/**
 * Clears the value of the <code>data-name</code> attribute for the given element
 * @param {Object} elem - the HTML element
 */
function clearDataName(elem) {
    setDataName(elem, "");
}

/**
 * Ensures the element matching the given ID has/doesn't have the given class. If the desired outcome is true before
 * invocation, no action is taken.
 * @param {string}  elemId    - the ID of the element
 * @param {string}  className - the name of the class
 * @param {boolean} toggleOn  - <code>true</code> if this element should contain the given class after this operation;
 *                              else false, if the class should be removed
 */
function toggleStyle(elemId, className, toggleOn) {
    const elem = document.getElementById(elemId);
    if (toggleOn && !elem.classList.contains(className)) {
        elem.classList.add(className);
    }
    else if (!toggleOn && elem.classList.contains(className)) {
        elem.classList.remove(className);
    }
}

/**
 * Removes from the element all classes which begin with <code>clazzStub</code>
 * @param {Object} elem      - the HTML element
 * @param {String} clazzStub - the text fragment which matching classes must begin with
 * @example
 * For the class stub "data-", "data-name" is a match and "my-data-name" is not
 */
function removeClassesStartingWith(elem, clazzStub) {
    let classToRemove;
    do {
        classToRemove = Array.from(elem.classList).find(clazz => clazz.startsWith(clazzStub));
        elem.classList.remove(classToRemove);
    }
    while (classToRemove);
}

/**
 * Gets the value of the <code>content</code> attribute for the <code>&lt;meta&gt;</code> tag with the given name
 * @param {string} name - the name of the <code>&lt;meta&gt;</code> element to return the value for
 */
function getMetaValue(name) {
    const metaElem = document.querySelector(`meta[name="${name}"]`);
    return metaElem ? metaElem.getAttribute("content") : null;
}

/**
 * Focuses on the element with the given ID. If the element is not naturally focusable, it's given a natural order
 * <code>tabindex</code> to make it focusable.
 * @param {string} elemId - the ID of the element to be focused
 */
function focusElement(elemId) {
    window.setTimeout(() => {
        const elem = document.getElementById(elemId);
        if (elem.tagName.toLowerCase() !== "a" &&
            elem.tagName.toLowerCase() !== "button" &&
            elem.tagName.toLowerCase() !== "input" &&
            elem.getAttribute("tabIndex") == null
        ) {
            // If the element wasn't already focusable, make it focusable
            elem.setAttribute("tabIndex", 0);
        }
        elem.focus();
    }, 0)
}

/**
 * Sets the inner HTML of the given element, with special handling for <code>&lt;script&gt;</code> tags to allow them to
 * run once added.
 * @param {Object} elem - the container
 * @param {string} html - the html to be added
 * @see https://stackoverflow.com/a/47614491/1908807
 */
function setInnerHTML(elem, html) {
    elem.innerHTML = html;

    Array.from(elem.querySelectorAll("script"))
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

/**
 * Remove all child nodes from the container element.
 * @param {Object} elem - the container to empty out
 */
function clearChildren(container) {
    while (container.firstChild) {
        container.removeChild(container.lastChild);
    }
}


/****************/
/*  HTTP Utils  */
/****************/

/**
 * Makes the request options for a Fetch API request. The body is optional; if a body is passed, the header
 * <code>Content-Type: application/json</code> will be added. If a <code>&lt;meta&gt;</code> tag is present with the
 * name <code>_csrf</code>, the value of that Cross-site Request Forgery token will be appended in the header
 * <code>X-CSRF-TOKEN</code>.
 * @param {!string} method - the HTTP method
 * @param {?Object} body   - an optional JSON payload
 */
function makeRequestOptions(method, body) {
    const hasBody = typeof body !== 'undefined';
    const csrfToken = getMetaValue("_csrf");
    const hasCsrf = typeof csrfToken !== 'undefined';

    return {
        method: method,
        headers: {
            ...(hasBody ? {"Content-Type": "application/json"} : {}),
            ...(hasCsrf ? {"X-CSRF-TOKEN": getMetaValue("_csrf")} : {})
        },
        ...(hasBody ? (hasBody ? {body: JSON.stringify(body)} : {body: body}) : {})
    };
}


/******************/
/*  Window Utils  */
/******************/

/**
 * Gets the part of the url that begins with <code>#</code>
 * @returns {string} the hash path, include the <code>#</code>, or empty string, if there is no hash path
 */
function hashPath() {
    return window.location.hash;
}

/**
 * Reloads the current window, sans the portion beginning with <code>#</code>
 */
function reloadWithoutHash() {
    window.location.replace(window.location.href.replace(window.location.hash, ""));
}

/**
 * Detects if the browse type is Chrome Mobile
 * @returns {boolean} <code>true</code> if the user agent string contains "Chrome {any version number} Mobile"
 */
function isChromeMobile() {
    return /Chrome\/[0-9\.]+ Mobile/i.test(navigator.userAgent);
}

/**
 * Flags the document body with certain classes based on the detected user agent
 */
function browserDetect() {
    if (isChromeMobile()) {
        console.debug("Mobile Chrome detected; adding CSS shim");
        document.body.classList.add("chrome-mobile-shim");
    }
}

/**
 * Sets the source of the given frame element
 * @param {string} frameElemId - the ID of the HTML frame element
 * @param {string} src         - the url to set for this frame
 */
function setFrameSrc(frameElemId, src) {
    document.getElementById(frameElemId).contentWindow.location.replace(src);
}