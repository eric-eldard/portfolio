<%-- A widget for displaying various project specs. All inputs are optional and hidden when not provided. --%>
<%@ page session="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<table class="specs">
    <c:if test="${param.frontend ne null}">
    <tr>
        <td>Frontend:</td>
        <td>${param.frontend}</td>
    </tr>
    </c:if>
    <c:if test="${param.backend ne null}">
    <tr>
        <td>Backend:</td>
        <td>${param.backend}</td>
    </tr>
    </c:if>
    <c:if test="${param.language ne null}">
        <tr>
            <td>Language:</td>
            <td>${param.language}</td>
        </tr>
    </c:if>
    <c:if test="${param.persistence ne null}">
    <tr>
        <td>Persistence:</td>
        <td>${param.persistence}</td>
    </tr>
    </c:if>
    <c:if test="${param.storage ne null}">
        <tr>
            <td>Storage:</td>
            <td>${param.storage}</td>
        </tr>
    </c:if>
    <c:if test="${param.caching ne null}">
        <tr>
            <td>Caching:</td>
            <td>${param.caching}</td>
        </tr>
    </c:if>
    <c:if test="${param.versioning ne null}">
    <tr>
        <td>Versioning:</td>
        <td>${param.versioning}</td>
    </tr>
    </c:if>
    <c:if test="${param.build ne null}">
    <tr>
        <td>Build<span class="mobile-hidden">&nbsp;Automation</span>:</td>
        <td>${param.build}</td>
    </tr>
    </c:if>
    <c:if test="${param.deployment ne null}">
    <tr>
        <td>Deployment:</td>
        <td>${param.deployment}</td>
    </tr>
    </c:if>
</table>