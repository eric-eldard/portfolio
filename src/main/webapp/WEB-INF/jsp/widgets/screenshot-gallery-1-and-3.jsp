<%--
    A 4 screenshot gallery with 1 central image and 3 in the right gutter.
    Hovering on the gutter images causes them to be shown in the central slot.
--%>

<div class="screenshot grid1and3" id="${param.id}">
    <a href="${param.centerImage}" target="_blank"><img src="${param.centerImage}" alt="${param.altText}"></a>
    <a href="${param.galleryImg1}" target="_blank"><img src="${param.galleryImg1}" alt="${param.altText}"></a>
    <a href="${param.galleryImg2}" target="_blank"><img src="${param.galleryImg2}" alt="${param.altText}"></a>
    <a href="${param.galleryImg3}" target="_blank"><img src="${param.galleryImg3}" alt="${param.altText}"></a>
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
        centerImage.src = "${param.centerImage}";
     });

    <%--
        Always return center image to original when mouse exits the container
        (prevents flickering when mouse moves over grid gaps, vs mouseout on each gallery image)
    --%>
    document.getElementById("${param.id}").addEventListener("mouseleave", (event) => {
        const centerImage = document.getElementById("${param.id}").children[0].children[0];
        centerImage.src = "${param.centerImage}";
     });
</script>