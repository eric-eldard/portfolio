const USER_ADMIN_PATH = "/portfolio/users";
const MIN_PASSWORD_CHARS = 8;

function postNewUser(newUser) {
    if (!newUser.username || newUser.username.trim().length < 0) {
        alert("Username cannot be blank")
    }
    else if (!newUser.password || newUser.password.trim().length < MIN_PASSWORD_CHARS) {
        alert(`User ${newUser.username} not created\nPassword must be at least ${MIN_PASSWORD_CHARS} characters`)
    }
    else {
        fetch(USER_ADMIN_PATH, makeRequestOptions("POST", newUser, "application/json"))
            .then(response => handleResponse(response));
    }
}

function deleteUser(id, username, successCallback = undefined) {
    const confirmed = confirm(`Are you sure you want to delete user ${username}?`);
    if (confirmed) {
        fetch(USER_ADMIN_PATH + "/" + id, makeRequestOptions("DELETE"))
            .then(response => handleResponse(response, successCallback));
    }
    else {
        alert(`Deletion of user ${username} canceled`);
    }
}

function unlockUser(id, username) {
    const confirmed = confirm("Confirm unlocking account for " + username);
    if (confirmed === true) {
        fetch(`${USER_ADMIN_PATH}/${id}/unlock`, makeRequestOptions("PATCH"))
            .then(response => handleResponse(response));
    }
}

function resetPword(id, username) {
    const password = prompt("Set new password for user " + username);
    if (typeof password === "undefined" || password === null) {
        alert(`Password change for user ${username} was canceled`);
    }
    else if (password.trim().length < MIN_PASSWORD_CHARS) {
        alert(`Password for user ${username} was not changed\nNew password must be at least ${MIN_PASSWORD_CHARS} characters`);
    }
    else {
        fetch(`${USER_ADMIN_PATH}/${id}/password`, makeRequestOptions("PATCH", password, "text/plain"))
            .then(response => handleResponse(response, (response => alert("Password updated for user " + username))));
    }
}

function setAuthUntil(id, username, date) {
    const confirmed = confirm(`Confirm ${date} as new authorized-until date for ${username}`);
    if (confirmed === true) {
            fetch(`${USER_ADMIN_PATH}/${id}/authorized-until/${date}`, makeRequestOptions("PATCH"))
                .then(response => handleResponse(response));
    }
    else {
        alert(`Authorized-until date for user ${username} not changed`);
        Window.location.reload();
    }
}

function setInfiniteAuth(id, username) {
    const confirmed = confirm("Confirm infinite authorization for " + username);
    if (confirmed === true) {
            fetch(`${USER_ADMIN_PATH}/${id}/authorized-until/forever`, makeRequestOptions("PATCH"))
                .then(response => handleResponse(response));
    }
    else {
        alert(`Authorized-until date for user ${username} not changed`);
        Window.location.reload();
    }
}

function toggleUser(id) {
    fetch(`${USER_ADMIN_PATH}/${id}/toggle-enabled`, makeRequestOptions("PATCH"))
        .then(response => handleResponse(response));
}

function toggleRole(id) {
    fetch(`${USER_ADMIN_PATH}/${id}/toggle-admin`, makeRequestOptions("PATCH"))
        .then(response => handleResponse(response));
}

function toggleAuth(id, authority) {
    fetch(`${USER_ADMIN_PATH}/${id}/toggle-auth/${authority}`, makeRequestOptions("PATCH"))
        .then(response => handleResponse(response));
}

/**
 * @param {successCallback} optional action to run on success; omit for default behavior (reload page)
 */
function handleResponse(response, successCallback = (response => window.location.reload())) {
    if (response.ok) {
        successCallback(response);
    }
    else {
        alert("Response from server: " + response.status);
    }
}