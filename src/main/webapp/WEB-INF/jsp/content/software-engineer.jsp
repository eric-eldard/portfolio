<h1>Software</h1>

<h2>Brazen Virtual Events</h2>
<p>
    In 2013, I made a career jump from running movie theaters to trying my hand at software engineering. I was
    incredibly fortunate to be picked up by the small startup Brazen. Over the first year, we built and launched
    Brazen's best-in-class virtual event platform. Then we spent the next 9 years building up and out, adding orders of
    magnitude more capacity, a customer-facing API, video chat, an embeddable chat plugin, multiple language support,
    advanced web accessibility, in-person event support, and literally hundreds more features. The Brazen software would
    host millions of users across tens-of-thousands of events.
</p>
<p>
    I started out in a junior Java developer role with Brazen, became lead for our solo Scrum team, and by the time
    Brazen was at its largest, I was Vice President of Engineering over three agile teams. In 2023, all of our hard work
    paid off when Brazen was acquired into the Radancy Talent Cloud. In 2024, I took over all of Brazen Engineering,
    adding QA, DevOps, and InfoSec to my teams.
</p>
<p>
    Building Brazen was an absolute pleasure, every day. I worked with such smart, talented, motivated, friendly people,
    and I made some connections that will last a lifetime. Brazen Engineering was a place to learn and try new things,
    and engineers grew very quickly there. I often estimated that a year in that fast-paced, people-focused environment
    was worth a year-and-a-half to two anywhere else.
</p>
<p>
    As a dev, I believe in writing code that is secure, readable, extensible, and efficient&mdash;in that order.
</p>
<p>
    Below is a little example of some of my Brazen work. In 2023, I was attached to a small tiger team, along with our
    VP of Marketing and our Data Engineering Manager, to experiment with both how we could include generative AI into
    our platform and how we could get AI tools into the hands of our team members. In a week, I put together a sample
    ChatGPT integration which would generate virtual event content for our customers, and also provide an open
    pass-through to ChatGPT for day-to-day usage by our staff.
</p>
<jsp:include page="../widgets/project-specs.jsp">
    <jsp:param name="frontend" value="Freemarker, Google Web Toolkit, Google Material Design, jQuery"/>
    <jsp:param name="backend" value="Java 17, Spring 5, Spring MVC, Spring Security, Akka, Jersey"/>
    <jsp:param name="persistence" value="Spring Data JPA"/>
    <jsp:param name="caching" value="Hazelcast"/>
    <jsp:param name="database" value="MySQL in Amazon RDS"/>
    <jsp:param name="versioning" value="Git via Bitbucket"/>
    <jsp:param name="build" value="Maven on Jenkins"/>
    <jsp:param name="env" value="Tomcat and Jetty on Amazon EC2"/>
</jsp:include>

<jsp:include page="../widgets/video-player.jsp">
    <jsp:param name="id" value="brazenite-gpt-video"/>
    <jsp:param name="iframe" value="${brazeniteGpt}"/>
</jsp:include>

<hr>

<h2>Tree of Usages</h2>
<p>
    As the VP of Engineering for a company with a monolith app, I was always looking for ways to help engineers
    better understand the impact of their changes. <b>Tree of Usages</b> is an IntelliJ plugin which helps engineers
    recursively trace possible call trees to see other areas of an application which their changes may ripple out to.
    Forked from a PhpStorm plugin designed to do the same, this was essentially rewritten from the ground-up to work for
    walking Java code. My version adds quality of life features, like a walkable history and duplicate-branch detection.
    I worked on this primarily as a hobby project, but encouraged all my engineers to use it after I released it to
    GitHub.
</p>

<jsp:include page="../widgets/see-it-on-gh.jsp">
    <jsp:param name="repo" value="Tree-of-Usages-Java"/>
</jsp:include>

<jsp:include page="../widgets/project-specs.jsp">
    <jsp:param name="language" value="Java 17, IntelliJ PSI SDK"/>
    <jsp:param name="versioning" value="GitHub"/>
    <jsp:param name="build" value="Gradle"/>
    <jsp:param name="env" value="IntelliJ"/>
</jsp:include>

<jsp:include page="../widgets/video-player.jsp">
    <jsp:param name="id" value="tree-of-usages-video"/>
    <jsp:param name="iframe" value="${treeOfUsages}"/>
</jsp:include>

<hr>

<h2>Plugin Performance Improvements</h2>
<p>
    I have a passion for performance, and in 2023, I noticed a growing portion of Brazen's traffic was coming from our
    embeddable chat plugin. While this was great news, this was also that feature that "kept me up at night." With no
    notice, our customers could add it to new pages or start marketing new jobs with it&mdash;for our largest customers,
    this could potentially result in millions of new plugin loads, overnight. While the call times were decent, it
    needed to be better. I took on improvements myself, adding some creative backend caching and reworking the client to
    request only the resources it would need in each plugin scenario. In the New Relic APM graphs below, you can see
    exactly when my changes were released to production.
</p>

<jsp:include page="../widgets/screenshot-solo.jsp" >
    <jsp:param name="path" value="/portfolio/assets/images/screenshots/plugin-db-improvements.png"/>
    <jsp:param name="altText" value="Plugin performance improvement graphs"/>
</jsp:include>