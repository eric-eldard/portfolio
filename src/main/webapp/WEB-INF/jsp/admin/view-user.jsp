<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<c:set var="user" value="${user}" scope="request"/>

<!DOCTYPE html>
<html lang="en">
    <head>
        <jsp:include page="../widgets/no-viewport-zoom.jsp"/>
        <jsp:include page="../widgets/csrf-token.jsp"/>
        <jsp:include page="../widgets/headers.jsp"/>

        <link rel="stylesheet" type="text/css" href="/public/assets/style/user-management.css">

        <style>
            .button-cell {
                display: grid;
                grid-template-columns: repeat(2, 1fr);
                justify-content: space-around;
                justify-items: center;
                width: 100%;
            }

            .button-cell button {
                width: 100px;
            }

            .vertical-table tr td {
                font-size: 12px;
                height: 22px;
                padding: 5px;
            }

            .vertical-table tr td:last-of-type {
                width: 120px;
            }
        </style>

        <script src="/public/assets/scripts/user-management.js"></script>
    </head>
    <body>
        <div class="top-container">
            <h1>View User: <b>${user.username}</b></h1>
            <div class="bread-crumbs">
                <a href="/">Home</a>
                <a href="/portfolio/">Portfolio</a>
                <a href="/portfolio/users">Users</a>
            </div>

            <table class="vertical-table">
                <tr>
                    <td>Authorized Until</td>
                    <td>
                        <c:import url="../widgets/authorized-until-input.jsp"/>
                    </td>
                </tr>
                <tr>
                    <td>Failed Password Attempts</td>
                    <td>${user.failedPasswordAttempts}</td>
                </tr>
                <c:if test="${user.lockedOn != null}">
                <tr>
                    <td>Locked On</td>
                    <td>${user.lockedOn}</td>
                </tr>
                </c:if>
                <tr>
                    <td>Disabled</td>
                    <td class="binary-field" onclick="toggleUser(${user.id})" title="${user.enabled ? 'Disable' : 'Enable'}">
                        ${!user.enabled ? "&cross;" : ""}
                    </td>
                </tr>
                <tr>
                    <td>Is Admin</td>
                    <td class="binary-field" onclick="toggleRole(${user.id})" title="${user.admin ? 'Demote' : 'Promote'}">
                        ${user.admin ? "&check;" : ""}
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <div class="button-cell">
                            <button onclick="resetPword(${user.id}, '${user.username}')">Set Password</button>
                            <button onclick="deleteUser(${user.id}, '${user.username}', (response => window.location = '/portfolio/users'));">Delete</button>
                        </div>
                    </td>
                </tr>
            </table>

            <c:choose>
                <c:when test="${empty user.loginAttempts}">
                    <h2>No Login Attempts</h2>
                </c:when>
                <c:otherwise>
                    <h2>Login Attempts</h2>
                    <table class="horizontal-table">
                        <tr>
                            <th>Timestamp</th>
                            <th>Outcome</th>
                            <th>Failure Reason</th>
                        </tr>
                        <c:forEach items="${user.getLoginAttemptsSorted()}" var="loginAttempt">
                            <tr>
                                <td style="${loginAttempt.outcome == 'FAILURE' ? 'color: #b00;' : ''}">${loginAttempt.timestamp}</td>
                                <td style="${loginAttempt.outcome == 'FAILURE' ? 'color: #b00;' : ''}">${loginAttempt.outcome}</td>
                                <td style="${loginAttempt.outcome == 'FAILURE' ? 'color: #b00;' : ''}">${loginAttempt.failureReason != null ? loginAttempt.failureReason : "&nbsp;"}</td>
                            </tr>
                        </c:forEach>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </body>
</html>