<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:choose>
    <c:when test="${empty param.error}">
        <div id="${param.wrapperId}" class="embedded-video">
            <script>
                Video.initPlayer("${param.wrapperId}", "${param.videoId}", "${param.token}");
            </script>
        </div>
    </c:when>
    <c:otherwise>
        <div class="embedded-video-error">
            ${param.error}
        </div>div>
    </c:otherwise>
</c:choose>