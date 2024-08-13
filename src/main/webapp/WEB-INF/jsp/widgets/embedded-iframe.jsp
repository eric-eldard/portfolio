<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="baseFrameDir" value="${param.baseFrameDir eq null ? '/portfolio/assets/projects' : param.baseFrameDir}"/>
<c:set var="baseImgDir" value="${param.baseImgDir eq null ? '/portfolio/assets/images/projects' : param.baseImgDir}"/>
<c:set var="frameUrl" value="${param.path.startsWith('http') ? param.path : baseFrameDir.concat('/').concat(param.path)}"/>

<div id="${param.id}" class="embedded-iframe">
    <div class="screenshot">
        <a href="${frameUrl}" target="_blank">
            <img src="${baseImgDir}/${param.backupImg}" alt="${param.description} screenshot (click to open)">
        </a>
    </div>

    <div class="iframe">
        <div>
            <iframe src="${frameUrl}"></iframe>
        </div>
    </div>
</div>