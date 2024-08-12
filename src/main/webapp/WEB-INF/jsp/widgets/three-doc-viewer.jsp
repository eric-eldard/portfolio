<div class="multi-doc">
    <div class="menu">
        <ul>
            <%--
                Frame needs to be updated from JS, instead of the links using the frame as their target, to avoid
                putting these docs into top-level page history (interferes with mobile back button behavior)
            --%>
            <li><a href="javascript: void(0)" onclick="setFrameSrc('brown-bag-viewer', '${param.doc1Path}#navpanes=0')">${param.doc1Name}</a></li>
            <li><a href="javascript: void(0)" onclick="setFrameSrc('brown-bag-viewer', '${param.doc2Path}#navpanes=0')">${param.doc2Name}</a></li>
            <li><a href="javascript: void(0)" onclick="setFrameSrc('brown-bag-viewer', '${param.doc3Path}#navpanes=0')">${param.doc3Name}</a></li>
        </ul>
    </div>
    <div class="document">
        <iframe id="brown-bag-viewer" src="${param.doc1Path}#navpanes=0" allowfullscreen></iframe>
    </div>
</div>