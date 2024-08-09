<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <jsp:include page="../widgets/headers.jsp"/>

        <link rel="stylesheet" type="text/css" href="/public/assets/style/user-management.css">

        <style>
            .horizontal-table tr td:last-of-type {
                border-width: 0 1px 1px 0;
                display: grid;
                font-family: unset;
                font-size: 0;
                padding: 0;
                white-space: nowrap;
            }

            .horizontal-table tr td:last-of-type button:not(:last-of-type) {
                margin: 3px 0 3px 3px;
            }

            .horizontal-table tr td:last-of-type button:last-of-type {
                margin: 3px;
            }

            .vertical-table tr td input {
                width: 250px;
            }

            @media screen and (min-width: 1000px) {
                .horizontal-table tr td:last-of-type {
                    display: table-cell;
                }
            }
        </style>

        <script src="/public/assets/scripts/user-management.js"></script>

        <script>
            function createUser() {
                postNewUser({
                    username:        document.getElementById("newUserName").value,
                    password:        document.getElementById("newUserPassword").value,
                    authorizedUntil: document.getElementById("newUserAuthUntil").value,
                    enabled:         document.getElementById("newUserEnabled").checked,
                    admin:           document.getElementById("newUserAdmin").checked,
                });
            }
        </script>
    </head>
    <body class="center-children">
        <h1>Portfolio User Management</h1>
        <div class="bread-crumbs center">
            <a href="/">Home</a>
            <a href="/portfolio/">Portfolio</a>
        </div>

        <fieldset>
            <legend>Create User</legend>
            <table class="vertical-table">
                <tr>
                    <td>Username</td>
                    <td><input type="text" id="newUserName" autocomplete="off"/></td>
                </tr>
                <tr>
                    <td>Password</td>
                    <td><input type="text" id="newUserPassword" autocomplete="off"/></td>
                </tr>
                <tr>
                    <td>Authorized Until</td>
                    <td><input type="date" id="newUserAuthUntil"/></td>
                </tr>
            </table>

            <div class="new-user-controls">
                <div>
                    <input type="checkbox" id="newUserEnabled" checked/>
                    <label for="newUserEnabled">Enabled</label>
                </div>

                <div>
                    <input type="checkbox" id="newUserAdmin"/>
                    <label for="newUserAdmin">Admin</label>
                </div>

                <button onclick="createUser()">Create</button>
            </div>
        </fieldset>

        <table class="horizontal-table">
            <tr>
                <th>ID</th>
                <th>Username</th>
                <th>Authorized Until</th>
                <th>Last Successful Login</th>
                <th>Failed Attempts</th>
                <th>Locked On</th>
                <th>Disabled</th>
                <th>Admin</th>
                <th>Actions</th>
            </tr>
            <c:forEach items="${userList}" var="user">
            <c:set var="user" value="${user}" scope="request"/>
            <tr>
                <td class="user-id"><a href="/portfolio/users/${user.id}" title="View user">${user.id}</a></td>
                <td>${user.username}</td>
                <td>
                    <c:import url="../widgets/authorized-until-input.jsp"/>
                </td>
                <td>${user.getLastSuccessfulLoginTimestamp() == null ? "<i>never</i>" : user.getLastSuccessfulLoginTimestamp()}</td>
                <td>${user.failedPasswordAttempts}</td>
                <td>
                    ${user.lockedOn}
                    <c:if test="${user.lockedOn != null}">
                        <a href="javascript: unlockUser(${user.id}, '${user.username}');" class="emoji-silhouette lock" title="Unlock">&#128275;</a>
                    </c:if>
                </td>
                <td class="binary-field" onclick="toggleUser(${user.id})" title="${user.enabled ? 'Disable' : 'Enable'}">
                    ${!user.enabled ? "&cross;" : ""}
                </td>
                <td class="binary-field" onclick="toggleRole(${user.id})" title="${user.admin ? 'Demote' : 'Promote'}">
                    ${user.admin ? "&check;" : ""}
                </td>
                <td>
                    <button onclick="resetPword(${user.id}, '${user.username}')">Password</button>
                    <button onclick="deleteUser(${user.id}, '${user.username}')">Delete</button>
                    &nbsp;<!-- prevents wonky collapsed border on mobile/grid view -->
                </td>
            </tr>
            </c:forEach>
        </table>
    </body>
</html>