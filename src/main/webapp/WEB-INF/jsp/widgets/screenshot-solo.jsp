<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="basedir" value="${param.basedir eq null ? '/portfolio/assets/images/screenshots' : param.basedir}"/>
<div class="screenshot solo ${param.classes eq null ? '' : param.classes}">
    <a href="${basedir}/${param.path}" target="_blank">
        <img src="${basedir}/${param.path}" alt="${param.description} screenshot (click to open)">
    </a>
</div>