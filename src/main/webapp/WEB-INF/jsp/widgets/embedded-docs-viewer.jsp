<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="basedir" value="/portfolio/assets/documents"/>
<c:set var="viewerParams" value="navpanes=0&view=Fit"/>

<div class="multi-doc">
    <div class="links">
        <ul>
            <li><a href="${basedir}/${param.doc1Path}" target="_blank">${param.doc1Name}</a></li>
            <li><a href="${basedir}/${param.doc2Path}" target="_blank">${param.doc2Name}</a></li>
            <li><a href="${basedir}/${param.doc3Path}" target="_blank">${param.doc3Name}</a></li>
        </ul>
    </div>
    <div class="menu">
        <ul>
            <%--
                Frame needs to be updated from JS, instead of the links using the frame as their target, to avoid
                putting these docs into top-level page history (interferes with mobile back button behavior)
            --%>
            <li><a href="javascript: void(0)" onclick="setFrameSrc('brown-bag-viewer', '${basedir}/${param.doc1Path}#${viewerParams}')">${param.doc1Name}</a></li>
            <li><a href="javascript: void(0)" onclick="setFrameSrc('brown-bag-viewer', '${basedir}/${param.doc2Path}#${viewerParams}')">${param.doc2Name}</a></li>
            <li><a href="javascript: void(0)" onclick="setFrameSrc('brown-bag-viewer', '${basedir}/${param.doc3Path}#${viewerParams}')">${param.doc3Name}</a></li>
        </ul>
    </div>
    <div class="document">
        <iframe id="brown-bag-viewer" src="${basedir}/${param.doc1Path}#${viewerParams}" allowfullscreen></iframe>
    </div>
</div>