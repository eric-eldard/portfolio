<h1>Portfolio App</h1>
<p>
    This portfolio is an app I built myself, initially written in <b>Spring Boot</b> 2.7 and then upgraded to 3.3. It
    features a mobile-friendly UI created from scratch, my own
    <jsp:include page="../widgets/link-out.jsp">
        <jsp:param name="text" value="JavaScript swipe-detection library"/>
        <jsp:param name="href" value="https://github.com/eric-eldard/swipe-events.js"/>
    </jsp:include>
    pre-compiled JSPs for faster load times, recursive asset preloading, third-party video integration, JWT auth tokens,
    and robust admin-only user management. I'm constantly adding and upgrading, and it's been a blast to work on!
</p>

<jsp:include page="../widgets/see-it-on-gh.jsp">
    <jsp:param name="repo" value="portfolio"/>
</jsp:include>

<jsp:include page="../widgets/project-specs.jsp">
    <jsp:param name="frontend" value="JSP 3 w/ JSTL, CSS3, JavaScript (ES6)"/>
    <jsp:param name="backend" value="Java 17, Spring Boot 3, Spring Web, Spring Security"/>
    <jsp:param name="persistence" value="Spring Data JPA"/>
    <jsp:param name="database" value="MySQL 8 in Amazon RDS"/>
    <jsp:param name="versioning" value="GitHub"/>
    <jsp:param name="build" value="Maven"/>
    <jsp:param name="env" value="Amazon EC2"/>
</jsp:include>

<jsp:include page="../widgets/screenshot-solo.jsp" >
    <jsp:param name="path" value="portfolio-hosting-infra.png"/>
    <jsp:param name="description" value="Portfolio Hosting Infrastructure diagram"/>
</jsp:include>

<h3>Admin-only user management</h3>
<jsp:include page="../widgets/screenshot-solo.jsp" >
    <jsp:param name="path" value="portfolio-user-management.png"/>
    <jsp:param name="description" value="Admin-only user management features"/>
    <jsp:param name="classes" value="white-border"/>
</jsp:include>
