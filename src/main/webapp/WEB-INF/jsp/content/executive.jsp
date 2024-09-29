<h1>Executive Work</h1>
<p>
    As Brazen's Vice President of Engineering, I was responsible for the quality of the product which engineers turned
    over to testers&mdash;and ultimately, to our customers. I consulted on major designs, took frequent code reviews,
    and encouraged best-practices. With a diverse team of engineers ranging from Principals to Year-One juniors, I took
    on a lot of different training and quality assurance initiatives. Guiding engineers to write code in secure,
    extensible, and maintainable ways has taught me more about software engineering than I ever learned from just
    writing code myself. And while I wrote a lot less code as an exec, it was also the time I wrote my best stuff.
</p>

<h2>Department Initiatives</h2>
<p>
    I tracked DORA metrics for Brazen Engineering, and here are some big initiatives I kicked off in order to improve
    specific deliverability and uptime stats.
</p>
<jsp:include page="../widgets/embedded-docs-viewer.jsp">
    <jsp:param name="doc1Name" value="Modularity"/>
    <jsp:param name="doc1Path" value="Modularity.pdf"/>
    <jsp:param name="doc2Name" value="Effective&nbsp;Code&nbsp;Reviews"/>
    <jsp:param name="doc2Path" value="Effective-Code-Reviews.pdf"/>
    <jsp:param name="doc3Name" value="Shift&nbsp;Left"/>
    <jsp:param name="doc3Path" value="Shift-Left.pdf"/>
</jsp:include>

<h3>Operational Success Criteria</h3>
<p>
    Brazen Engineering always had good alarms for things that absolutely couldn't fail, like a server going down. But
    there are situations for which failure is expected <i>some</i> of the time and constant alarms would be very noisy.
    However, there's a big difference between <i>10% failed logins</i> and <i>90% failed logins</i>; at some point, you
    cross the threshold into <i>we need to know</i> territory. After identifying Mean Time to Detect as our most lagging
    process metric, I created the Operational Success Criteria program. This saw our engineering teams instrumenting
    their most critical endpoints and pushing metrics into Google BigQuery. The teams then built Sigma dashboards on top
    of that data, allowing them to begin their standups with a health check of their products.
</p>
<jsp:include page="../widgets/screenshot-solo.jsp">
    <jsp:param name="path"        value="Operational-Success-Flow.png"/>
    <jsp:param name="description" value="Flowchart for Operational Success Criteria initiative"/>
</jsp:include>

<hr>

<h2>Lessons in 60s video series</h2>
<p>
    Brown bags can be a big time investment for a team. I've often spent 20 hours assembling a high quality
    lunch-and-learn session&mdash;and the team will spend another 20 collective hours attending. I do love the
    participation you get with a live meeting, but a lot of topics don't require pulling engineers away from doing their
    actual work. In 2022, I started the <b>Lessons in 60s series</b> in which I'd share 60-second training videos with
    my team, that they could consume whenever convenient. In total, I put about a dozen lessons together and encouraged
    my team to share their knowledge in the same format, which brought in another dozen videos on diverse topics.
</p>
<p>
    Here's an example from a set of videos I did on being an IntelliJ power user.
</p>
<jsp:include page="../widgets/video-player.jsp">
    <jsp:param name="wrapperId" value="intellij-postfix-video"/>
    <jsp:param name="videoId"   value="${INTELLIJ_POSTFIX_VIDEO_ID}"/>
    <jsp:param name="token"     value="${INTELLIJ_POSTFIX_VIDEO_TOKEN}"/>
    <jsp:param name="error"     value="${INTELLIJ_POSTFIX_VIDEO_ERROR}"/>
</jsp:include>

<hr>

<h2>2022 Hackathon</h2>
<p>
    Brazen expanded rapidly in 2020 & 2021&mdash;suddenly our team was spread all over two continents. In 2022, we
    brought the whole engineering department together at Brazen HQ for a hackathon. Engineers worked in groups of their
    choice and selected their own projects. It was a great bonding activity and a lot of fun was had. My role was to
    consult on all projects, helping devs generate their ideas and scope them down to something achievable in 48 hours,
    then stay abreast of each team's progress, ensuring they would have something in a presentable state by the end.
    Heads from all Brazen departments participated in project judging, and we were all extremely impressed by the
    quality and creativity delivered by our engineers. In the end, our hackathon was seen by all as a huge success, both
    culturally and for the work it produced. Though we had tempered expectations that the goal wasn't new feature
    development, within six months 6 of 14 hackathon projects had gone into production.
</p>
<p>
    I cut this video of the presentations, along with some B-roll, to share our
    engineers' hard work and enthusiasm with the whole company.
</p>
<jsp:include page="../widgets/video-player.jsp">
    <jsp:param name="wrapperId" value="hackathon-video"/>
    <jsp:param name="videoId"   value="${HACKATHON_VIDEO_ID}"/>
    <jsp:param name="token"     value="${HACKATHON_VIDEO_TOKEN}"/>
    <jsp:param name="error"     value="${HACKATHON_VIDEO_ERROR}"/>
</jsp:include>
