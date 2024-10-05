<style>
    #resume-doc {
        bottom: 25px;
        left: 25px;
        position: absolute;
        right: 25px;
        top: 40px;
        width: auto;
    }

    #resume-doc .iframe {
        display: block !important;  /* no backup image (the resume popup doesn't display on smaller screens;
                                       this display property is just for window resizing by the user) */
        height: 100%;
    }
</style>

<%-- No backup image because resume gets alternate treatment on mobile, instead of opening the popup --%>
<jsp:include page="../widgets/embedded-doc-solo.jsp">
    <jsp:param name="id"   value="resume-doc"/>
    <jsp:param name="path" value="Eric-Eldard-Resume.pdf"/>
</jsp:include>