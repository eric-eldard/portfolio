<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <jsp:include page="widgets/no-viewport-zoom.jsp"/>
        <jsp:include page="widgets/headers.jsp"/>
    </head>

    <body onload="ensureNotPopupState()">
        <div id="main">
            <jsp:include page="content/title-subtitle.jsp"/>

            <div class="content">
                <div class="intro-heading">Get to know me as a...</div>
                <jsp:include page="content/timeline.jsp"/>
                <jsp:include page="content/footer.jsp"/>
            </div>

        </div>

        <div id="popup-background" onclick="closePopup()">
            <div id="popup" onclick="event.stopPropagation()"></div>
        </div>

        <div id="preload-images">
            <c:forEach items="${SCREENSHOTS}" var="image"><input type="hidden" value="${image}"/>
            </c:forEach>
        </div>

        <div id="preload-docs">
            <c:forEach items="${DOCUMENTS}" var="doc"><input type="hidden" value="${doc}"/>
            </c:forEach>
        </div>

        <div id="loaded-docs"></div>

        <script async defer>
            // Pre-load images
            let imagePaths = document.getElementById("preload-images").getElementsByTagName('input');
            for (let path of imagePaths) {
                fetch(path.value);
            }
            document.getElementById("preload-images").remove();

            // Pre-load PDFs; AJAX-fetched PDFs do not count as cached for when the doc is used as a top-level source,
            // so we'll fetch each doc in an iframe now (the same way they'll be retrieved when they're actually shown)
            let loadedDocs = document.getElementById("loaded-docs");
            let docPaths = document.getElementById("preload-docs").getElementsByTagName('input');
            for (let path of docPaths) {
                let frame = document.createElement("iframe");
	            frame.src = path.value;
	            frame.style.display = "none";
	            loadedDocs.appendChild(frame);
            }
            document.getElementById("preload-docs").remove();
        </script>
    </body>
</html>