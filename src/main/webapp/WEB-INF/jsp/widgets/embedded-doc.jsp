<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="fit" value="${param.fitOverride eq null ? 'FitH' : param.fitOverride}"/>
<div id="${param.id}" class="embedded-doc">
    <iframe src="/portfolio/assets/documents/${param.file}#navpanes=0&view=${fit}" allowfullscreen></iframe>
</div>