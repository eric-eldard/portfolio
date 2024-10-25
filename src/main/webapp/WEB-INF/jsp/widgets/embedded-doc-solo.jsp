<%@ page session="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="baseDocDir" value="${param.baseDocDir eq null ? '/portfolio/assets/documents' : param.baseDocDir}"/>
<c:set var="fit" value="${param.fitOverride eq null ? 'FitH' : param.fitOverride}"/>

<div id="${param.id}" class="embedded-doc">
    <c:if test="${param.backupImg ne null}">
        <c:set var="baseImgDir" value="${param.baseImgDir eq null ? '/portfolio/assets/images/documents' : param.baseImgDir}"/>
        <c:set var="description" value="${param.description eq null ? param.path : param.description}"/>
        <div class="screenshot">
            <a href="${baseDocDir}/${param.path}" target="_blank">
                <img src="${baseImgDir}/${param.backupImg}" alt="${description} screenshot (click to open)">
            </a>
        </div>
    </c:if>

    <div class="iframe">
        <div>
            <iframe src="${baseDocDir}/${param.path}#navpanes=0&view=${fit}" allowfullscreen></iframe>
        </div>
    </div>
</div>