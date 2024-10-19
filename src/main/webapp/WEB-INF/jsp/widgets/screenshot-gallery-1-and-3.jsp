<%--
    A 4 screenshot gallery with 1 central image and 3 in the right gutter.
    Hovering on the gutter images causes them to be shown in the central slot.
--%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="basedir"   value="${param.basedir eq null ? '/portfolio/assets/images/screenshots' : param.basedir}"/>
<c:set var="centerImg" value="${basedir}/${param.centerImage}"/>
<c:set var="img1"      value="${basedir}/${param.galleryImg1}"/>
<c:set var="img2"      value="${basedir}/${param.galleryImg2}"/>
<c:set var="img3"      value="${basedir}/${param.galleryImg3}"/>
<c:set var="altText"   value="${param.description} screenshot (click to open)"/>

<div class="screenshot grid1and3 ${param.classes}" id="${param.id}">
    <a href="javascript:void(0)" class="focused" title="Open in new tab">
        <img src="${centerImg}" alt="${altText}">
    </a>
    <a href="javascript:void(0)" class="top" title="Click to view">
        <img src="${img1}" alt="${altText}">
    </a>
    <a href="javascript:void(0)" class="middle" title="Click to view">
        <img src="${img2}" alt="${altText}">
    </a>
    <a href="javascript:void(0)" class="bottom" title="Click to view">
        <img src="${img3}" alt="${altText}">
    </a>
</div>

<script>
    <%-- Add click behaviors to all gallery images --%>
    for (let i = 0; i < document.getElementById("${param.id}").childElementCount; i++) {
        const child = document.getElementById("${param.id}").children[i];

        child.addEventListener("click", (event) => {
            const focusedImage = document.querySelector("#${param.id} > .focused");

            <%-- When a prior animation is still running, ignore this click --%>
            if (focusedImage.style.animationPlayState === "running") {
                return;
            }
            <%-- When the focused image is clicked, open it in a new window --%>
            else if (focusedImage === child) {
                window.open(child.children[0].src, "_blank");
            }
            <%-- When any non-focused element is clicked, swap it with the focused element --%>
            else {
                <%-- Start the rotation animation for both images --%>
                focusedImage.style.animationPlayState = "running";
                child.style.animationPlayState = "running";

                <%-- The backface of each image shows between 25% and 75% of the way through the animation;
                     swap the focused and clicked images in this window --%>
                window.setTimeout(() => {
                    focusedImage.className = child.className;
                    child.className = "focused";

                    focusedImage.title = child.title;
                    child.title = "Open in new tab";
                }, 400);
            }
        });

        <%-- Reset the css animation after it's played --%>
        child.addEventListener("animationend", (event) => Portfolio.resetAnimation(child));
    }
</script>