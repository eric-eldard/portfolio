<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="basedir" value="/portfolio/assets/documents"/>
<c:set var="fit" value="${param.fitOverride eq null ? 'FitH' : param.fitOverride}"/>
<div id="${param.id}" class="embedded-doc">
    <jsp:include page="../widgets/open-in-new-tab.jsp">
        <jsp:param name="href" value="${basedir}/${param.file}"/>
    </jsp:include>
    <iframe src="${basedir}/${param.file}#navpanes=0&view=${fit}" allowfullscreen></iframe>
</div>