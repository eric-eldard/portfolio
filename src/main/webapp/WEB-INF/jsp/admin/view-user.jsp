<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <jsp:include page="../widgets/headers.jsp"/>
        <jsp:include page="../widgets/csrf-token.jsp"/>

        <link rel="stylesheet" type="text/css" href="/public/assets/style/user-management.css">

        <style>
            .vertical-table tr td {
                font-size: 12px;
                padding: 5px;
            }

            .vertical-table tr td:not(first-of-type) {
                min-width: 75px;
            }
        </style>

        <script src="/public/assets/scripts/user_management.js"></script>
    </head>
    <body class="center-children">
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
                    <c:choose>
                        <c:when test="${user.authorizedUntil == null}">
                            <i>forever</i>
                        </c:when>
                        <c:otherwise>
                            <fmt:formatDate pattern="yyyy-MM-dd" value="${user.authorizedUntil}"/>
                        </c:otherwise>
                    </c:choose>
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
                <td class="binary-field">${!user.enabled ? "&cross;" : ""}</td>
            </tr>
            <tr>
                <td>Is Admin</td>
                <td class="binary-field">${user.admin ? "&check;" : ""}</td>
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
    </body>
</html>