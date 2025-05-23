<%@ page session="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <jsp:include page="widgets/headers.jsp"/>
        <link rel="stylesheet" type="text/css" href="/public/assets/style/footer.css">
        <link rel="stylesheet" type="text/css" href="/public/assets/style/timeline.css">
        <link rel="stylesheet" type="text/css" href="/public/assets/style/timeline-impl.css">
    </head>

    <body>
        <main>
            <jsp:include page="content/title-subtitle.jsp"/>

            <div class="content">
                <div class="intro-heading">Get to know me as a...</div>
                <jsp:include page="content/timeline.jsp"/>
                <jsp:include page="content/footer.jsp"/>
            </div>

        </main>

        <div id="popup-container" onclick="Portfolio.closePopup()">
            <div id="popup" onclick="event.stopPropagation()">
                <div id="top-controls">
                    <a href="javascript: Portfolio.closePopup();" id="closeX" title="Close"></a>
                </div>
                <a href="javascript: Portfolio.jumpToPrevious();" id="move-back" class="popup-nav" title="Move back"></a>
                <a href="javascript: Portfolio.jumpToNext();" id="move-forward" class="popup-nav" title="Move forward"></a>
                <div id="popup-content"></div>
            </div>
        </div>
        <div id="swipe-indicators"></div>

        <div id="preload-images">
            <c:forEach items="${IMAGES}" var="image"><input type="hidden" value="${image}"/>
            </c:forEach> <%-- new line for end tag ensures each input gets a separate line in final html doc --%>
        </div>

        <div id="preload-docs">
            <c:forEach items="${DOCUMENTS}" var="doc"><input type="hidden" value="${doc}"/>
            </c:forEach> <%-- new line for end tag ensures each input gets a separate line in final html doc --%>
        </div>

        <div id="loaded-docs"></div>

        <script async>
            // Pre-load images
            const imagePaths = document.getElementById("preload-images").getElementsByTagName('input');
            for (let path of imagePaths) {
                fetch(path.value);
            }
            console.log(imagePaths.length + " portfolio images preloaded");
            document.getElementById("preload-images").remove();

            // Pre-load PDFs; AJAX-fetched PDFs do not count as cached for when the doc is used as a top-level source,
            // so we'll fetch each doc in an iframe now (the same way they'll be retrieved when they're actually shown)
            let loadedDocs = document.getElementById("loaded-docs");
            const docPaths = document.getElementById("preload-docs").getElementsByTagName('input');
            for (let path of docPaths) {
                let frame = document.createElement("iframe");
	            frame.src = path.value;
	            frame.style.display = "none";
	            loadedDocs.appendChild(frame);
            }
            console.log(docPaths.length + " portfolio documents preloaded");
            document.getElementById("preload-docs").remove();
        </script>

        <script defer>
            Portfolio.openPopupIfPopupState();
        </script>
    </body>
</html>