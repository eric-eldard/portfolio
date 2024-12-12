<%@ page session="false" %>
<h1>Software</h1>

<p>
    In 2013, I made a career jump from running movie theaters to trying my hand at software engineering. I was
    incredibly fortunate to be picked up by the small startup Brazen. Over the first year, we built and launched
    Brazen's best-in-class virtual event and recruitment chat platform. Then we spent the next 9 years building up and
    out, adding orders of magnitude more capacity, a customer-facing API, video chat, an embeddable chat plugin,
    international language support, advanced web accessibility, in-person event support, and literally hundreds more
    features. The Brazen software would host millions of users across tens-of-thousands of events.
</p>
<p>
    I started out in a junior Java developer role with Brazen, became the lead for our only Scrum team, and by the time
    Brazen was at its largest, I was Vice President of Engineering over three agile teams. In 2023, all of our hard work
    paid off when Brazen was acquired into the Radancy Talent Cloud. Alongside Brazen's Chief Technology Officer, I had
    participated in several rounds of technical diligence. The confidence we fostered in the robustness of our stack was
    cited as a key factor in Brazen's acquisition. In 2024, I took over all of Brazen Engineering, adding QA, DevOps,
    InfoSec, and Tier 2 Support to my teams.
</p>
<p>
    I believe in creating software which is secure, testable, readable, extensible, and efficient&mdash;in that order.
</p>

<h2>Brazen Virtual Events</h2>
<p>
    Below is a small sample of my Brazen work. In 2023, I was attached to a tiger team, along with our VP of Marketing
    and our Data Engineering Manager, to experiment with how we could include generative AI into our platform. Over a
    couple of days, I put together this proof-of-concept ChatGPT integration which could generate virtual event content
    for our customers.
</p>
<jsp:include page="../widgets/project-specs.jsp">
    <jsp:param name="frontend" value="Freemarker, Google Web Toolkit, Google Material Design, jQuery"/>
    <jsp:param name="backend" value="Java 17, Spring 5, Spring Web, Spring Security, Akka, Jersey"/>
    <jsp:param name="ai" value="gpt-3.5-turbo"/>
    <jsp:param name="persistence" value="Spring Data JPA"/>
    <jsp:param name="storage" value="MySQL 8 in Amazon RDS, Amazon S3, Cassandra"/>
    <jsp:param name="caching" value="Hazelcast"/>
    <jsp:param name="versioning" value="Bitbucket (Git)"/>
    <jsp:param name="build" value="Maven, Jenkins"/>
    <jsp:param name="deployment" value="Tomcat and Jetty on <span class='mobile-hidden'>Amazon </span>EC2"/>
</jsp:include>

<jsp:include page="../widgets/video-player.jsp">
    <jsp:param name="wrapperId" value="brazenite-gpt-video"/>
    <jsp:param name="videoId"   value="${BRAZENITE_GPT_VIDEO_ID}"/>
    <jsp:param name="token"     value="${BRAZENITE_GPT_VIDEO_TOKEN}"/>
    <jsp:param name="error"     value="${BRAZENITE_GPT_VIDEO_ERROR}"/>
</jsp:include>

<hr>

<h2>Tree of Usages</h2>
<p>
    As the VP of Engineering for a company with a monolith app, I was always looking for ways to help engineers
    better understand the impact of their changes. <b>Tree of Usages</b> is an IntelliJ plugin which helps engineers
    recursively trace possible call trees to see other areas their changes may ripple out to. Forked from a PhpStorm
    plugin designed to do the same, it was rewritten to work for walking Java code. My version adds quality of life
    features, like a navigable history and duplicate-branch detection.
</p>

<jsp:include page="../widgets/see-it-on-gh.jsp">
    <jsp:param name="repo" value="Tree-of-Usages-Java"/>
</jsp:include>

<jsp:include page="../widgets/project-specs.jsp">
    <jsp:param name="language" value="Java 17, IntelliJ PSI SDK"/>
    <jsp:param name="versioning" value="GitHub"/>
    <jsp:param name="build" value="Gradle"/>
    <jsp:param name="deployment" value="IntelliJ plugin"/>
</jsp:include>

<jsp:include page="../widgets/video-player.jsp">
    <jsp:param name="wrapperId" value="tree-of-usages-video"/>
    <jsp:param name="videoId"   value="${TREE_OF_USAGES_VIDEO_ID}"/>
    <jsp:param name="token"     value="${TREE_OF_USAGES_VIDEO_TOKEN}"/>
    <jsp:param name="error"     value="${TREE_OF_USAGES_VIDEO_ERROR}"/>
</jsp:include>

<hr>

<h2>Plugin Performance Improvements</h2>
<p>
    <b>I have a passion for performance.</b> In 2023, I noticed the fastest-growing portion of Brazen's traffic was
    coming from our embeddable chat plugin. The call times were decent, but to continue indefinite growth, they'd need
    to be better. I took on the improvements myself, optimizing queries, adding backend caching, and reworking the
    client to request only the resources it would need in each plugin scenario. In the New Relic APM graphs below, you
    can see exactly when my changes were released to production.
</p>

<jsp:include page="../widgets/screenshot-solo.jsp">
    <jsp:param name="path"        value="plugin-db-improvements.png"/>
    <jsp:param name="description" value="Plugin performance improvement graphs"/>
</jsp:include>