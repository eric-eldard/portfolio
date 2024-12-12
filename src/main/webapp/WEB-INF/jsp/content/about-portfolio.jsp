<%@ page session="false" %>
<h1>Portfolio App</h1>
<p>
    This portfolio is an app I built myself. It showcases some of my tried-and-true technologies, like <b>AWS</b> and
    <b>Spring Core/Data/Security</b>, as well as some new ones for me, like <b>Spring Boot</b> and <b>TypeScript</b>.
    It features a mobile-responsive UI created from scratch, my own
    <jsp:include page="../widgets/link-out.jsp">
        <jsp:param name="text" value="JavaScript swipe-detection library"/>
        <jsp:param name="href" value="https://github.com/eric-eldard/swipe-events.js"/>
    </jsp:include>,
    pre-compiled JSPs and recursive asset preloading for faster load times, third-party video integration, stateless
    auth through JSON Web Tokens, and robust user management. I'm constantly adding and upgrading, and it's been a blast
    to work on!
</p>

<jsp:include page="../widgets/see-it-on-gh.jsp">
    <jsp:param name="repo" value="portfolio"/>
</jsp:include>

<jsp:include page="../widgets/project-specs.jsp">
    <jsp:param name="frontend" value="JSP 3, CSS3, TypeScript"/>
    <jsp:param name="backend" value="Java 21, Spring Boot 3, Spring Web, Spring Security"/>
    <jsp:param name="persistence" value="Spring Data JPA"/>
    <jsp:param name="storage" value="MySQL 8 in Amazon RDS"/>
    <jsp:param name="versioning" value="GitHub"/>
    <jsp:param name="build" value="Maven"/>
    <jsp:param name="deployment" value="Amazon EC2"/>
</jsp:include>

<h4>Cloud Architecture Diagram</h4>
<jsp:include page="../widgets/screenshot-solo.jsp">
    <jsp:param name="path"        value="portfolio-hosting-infra.png"/>
    <jsp:param name="description" value="Portfolio Hosting Infrastructure diagram"/>
</jsp:include>

<h4>Mobile-Responsive Design</h4>
<jsp:include page="../widgets/video-player.jsp">
    <jsp:param name="wrapperId" value="responsive-design-video"/>
    <jsp:param name="classes"   value="aspect-1680x1080 square-border white-border"/>
    <jsp:param name="videoId"   value="${RESPONSIVE_DESIGN_VIDEO_ID}"/>
    <jsp:param name="token"     value="${RESPONSIVE_DESIGN_VIDEO_TOKEN}"/>
    <jsp:param name="error"     value="${RESPONSIVE_DESIGN_VIDEO_ERROR}"/>
</jsp:include>

<h4>Authentication Flows</h4>
<jsp:include page="../widgets/screenshot-solo.jsp">
    <jsp:param name="path"        value="portfolio-login-flow.png"/>
    <jsp:param name="description" value="Portfolio login flowchart"/>
</jsp:include>
<br>
<jsp:include page="../widgets/screenshot-solo.jsp">
    <jsp:param name="path"        value="portfolio-request-flow.png"/>
    <jsp:param name="description" value="Portfolio request flowchart"/>
</jsp:include>

<h4>Admin-only User Management</h4>
<jsp:include page="../widgets/screenshot-solo.jsp">
    <jsp:param name="path"        value="portfolio-user-management.png"/>
    <jsp:param name="description" value="Admin-only user management features"/>
    <jsp:param name="classes"     value="white-border"/>
</jsp:include>
