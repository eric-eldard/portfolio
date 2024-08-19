<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%-- A widget for displaying various project specs. All inputs are optional and hidden when not provided. --%>

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
    <c:if test="${param.database ne null}">
        <tr>
            <td>Database:</td>
            <td>${param.database}</td>
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
        <td>Build Automation:</td>
        <td>${param.build}</td>
    </tr>
    </c:if>
    <c:if test="${param.env ne null}">
    <tr>
        <td>Environment:</td>
        <td>${param.env}</td>
    </tr>
    </c:if>
</table>