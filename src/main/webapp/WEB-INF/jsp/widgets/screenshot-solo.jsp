<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="basedir" value="${param.basedir eq null ? '/portfolio/assets/images/screenshots' : param.basedir}"/>
<c:set var="imgUrl" value="${basedir}/${param.path}"/>
<c:set var="linkUrl" value="${param.href eq null ? imgUrl : param.href}"/>
<div class="screenshot solo ${param.classes}">
    <a href="${linkUrl}" target="_blank">
        <img src="${imgUrl}" alt="${param.description} screenshot (click to open)">
    </a>
</div>