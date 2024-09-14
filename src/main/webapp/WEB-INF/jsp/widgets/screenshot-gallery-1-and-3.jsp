<%--
    A 4 screenshot gallery with 1 central image and 3 in the right gutter.
    Hovering on the gutter images causes them to be shown in the central slot.
--%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="basedir" value="${param.basedir eq null ? '/portfolio/assets/images/screenshots' : param.basedir}"/>
<c:set var="centerImg" value="${basedir}/${param.centerImage}"/>
<c:set var="img1" value="${basedir}/${param.galleryImg1}"/>
<c:set var="img2" value="${basedir}/${param.galleryImg2}"/>
<c:set var="img3" value="${basedir}/${param.galleryImg3}"/>
<c:set var="altText" value="${param.description} screenshot (click to open)"/>

<div class="screenshot grid1and3" id="${param.id}">
    <a href="${centerImg}" target="_blank"><img src="${centerImg}" alt="${altText}"></a>
    <a href="${img1}" target="_blank"><img src="${img1}" alt="${altText}"></a>
    <a href="${img2}" target="_blank"><img src="${img2}" alt="${altText}"></a>
    <a href="${img3}" target="_blank"><img src="${img3}" alt="${altText}"></a>
</div>

<script>
    <%-- When each gallery image is moused-over, show it in the center slot --%>
    for (let i = 1; i < document.getElementById("${param.id}").childElementCount; i++) {
         let child = document.getElementById("${param.id}").children[i];
         child.addEventListener("mouseover", (event) => {
             const centerImage = document.getElementById("${param.id}").children[0].children[0];
             const thisImage = child.children[0];
             centerImage.src = thisImage.src;
         });
     }

    <%-- When center image is moused-over, return to original --%>
    document.getElementById("${param.id}").children[0].addEventListener("mouseover", (event) => {
        const centerImage = document.getElementById("${param.id}").children[0].children[0];
        centerImage.src = "${centerImg}";
    });

    <%--
        Always return center image to original when mouse exits the container
        (prevents flickering when mouse moves over grid gaps, vs mouseout on each gallery image)
    --%>
    document.getElementById("${param.id}").addEventListener("mouseleave", (event) => {
        const centerImage = document.getElementById("${param.id}").children[0].children[0];
        centerImage.src = "${centerImg}";
    });
</script>