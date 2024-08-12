<h1>Portfolio App</h1>
<p>
    This portfolio is a Spring Boot app I wrote myself. It was a blast to put together!
</p>

<jsp:include page="../widgets/see-it-on-gh.jsp">
    <jsp:param name="repo" value="portfolio"/>
</jsp:include>

<jsp:include page="../widgets/project-specs.jsp">
    <jsp:param name="frontend" value="JSP, CSS3, JavaScript (custom UI from scratch; no toolkits)"/>
    <jsp:param name="backend" value="Java 17, Spring Boot 2.7, Spring Security"/>
    <jsp:param name="persistence" value="Spring Data JPA"/>
    <jsp:param name="database" value="MySQL in Amazon RDS"/>
    <jsp:param name="versioning" value="GitHub"/>
    <jsp:param name="build" value="Maven"/>
    <jsp:param name="env" value="Amazon EC2"/>
</jsp:include>

<jsp:include page="../widgets/screenshot-solo.jsp" >
    <jsp:param name="path" value="/portfolio/assets/images/screenshots/portfolio-hosting-infra.png"/>
    <jsp:param name="altText" value="Portfolio Hosting Infrastructure diagram"/>
</jsp:include>

<h3>Admin-only user management</h3>
<jsp:include page="../widgets/screenshot-solo.jsp" >
    <jsp:param name="path" value="/portfolio/assets/images/screenshots/portfolio-user-management.png"/>
    <jsp:param name="altText" value="Admin-only user management features screenshot"/>
    <jsp:param name="classes" value="white-border"/>
</jsp:include>
