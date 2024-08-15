<style>
    #flowchart-doc .iframe {
        aspect-ratio: 128 / 69;
    }

    @media screen and (min-width: 600px) {
        #dfd-doc .iframe, #partner-doc .iframe {
            height: 575px;
        }

        #flowchart-doc .iframe {
            height: 550px;
        }
    }
</style>

<h1>Solutions</h1>

<p>
    For most of its life, Brazen didn't have a <i>solutions architect</i> position, leaving that responsibility to a mix
    of product owners and engineers. With a background in customer service, I was happy to jump on customer calls to
    figure out how Brazen products could satisfy their needs.
</p>
<p>
    This is a role I would play a good deal as Brazen became part of the Radancy product suite, and we designed how we
    would integrate into the rest of the stack over the first 2 years.
</p>
<p>
    As a provider of solutions, I believe in clean, attractive visuals. Putting in time upfront to ensure quality
    alleviates confusion and saves time for your audience when they're trying to absorb a new concept.
</p>

<h2>Brazen Partner Integration</h2>
<p>
    In 2022, Brazen worked with a major job board to use Brazen Virtual Events as the interview component of their
    hiring funnel. After consulting with their product and tech folks on the need, I put together this interactive
    sequence diagram, illustrating the fastest path to achieving this integration. This really helped to advance our
    conversation.
</p>
<p class="disclaimer">
    [Partner information anonymized]
</p>
<jsp:include page="../widgets/embedded-doc-solo.jsp">
    <jsp:param name="id" value="partner-doc"/>
    <jsp:param name="path" value="Partner-Integration.pdf"/>
    <jsp:param name="backupImg" value="Partner-Integration.png"/>
    <jsp:param name="description" value="Partner Integration Sequence Diagram"/>
</jsp:include>

<hr>

<h2>developers.brazen.com</h2>
<p>
    <jsp:include page="../widgets/link-out.jsp">
        <jsp:param name="text" value="Brazen Developer Resources"/>
        <jsp:param name="href" value="https://developers.brazen.com/"/>
    </jsp:include>
    advertises all the ways customers and partners can integrate with Brazen. This portal was created and maintained by
    me and one of my long-time Brazen collaborators. It showcases some solutions I helped design and build from the
    ground-up, like our customer-facing RESTful API, our XML job feeds, and our embeddable chat plugin. I also staffed
    developers@brazen.com, assisting our customers' tech folks as they built their integrations. I love building tools
    that facilitate other devs building cool stuff!
</p>
<jsp:include page="../widgets/screenshot-solo.jsp" >
    <jsp:param name="path" value="brazen-dev-resources.png"/>
    <jsp:param name="description" value="Brazen Developer Resources"/>
</jsp:include>

<hr>

<h2>Brazen Data Flows</h2>
<p>
    When Brazen joined the Radancy family, I put this data flow diagram together to help our new teammates understand
    what kind of data we worked with and how it was enriched as it moved through our system&mdash;and ultimately which
    points in the Brazen candidate journey would provide the highest value for integration. I produced both this YC
    version and a multi-level GC version, which can be easier to digest for folks unfamiliar with your system.
</p>
<jsp:include page="../widgets/embedded-doc-solo.jsp">
    <jsp:param name="id" value="dfd-doc"/>
    <jsp:param name="path" value="Candidate-and-Job-Data-Flows.pdf"/>
    <jsp:param name="backupImg" value="Candidate-and-Job-Data-Flows.png"/>
    <jsp:param name="description" value="Candidate and Job Data Flow Diagram"/>
</jsp:include>

<hr>

<h2>Brazen Event Capacity Flow</h2>
<p>
    I put this flowchart together for a customer who was looking to better understand the interplay of several advanced
    features of our event sign-up flow. The use of screenshots here helps tie abstract concepts to our more familiar ui.
</p>
<jsp:include page="../widgets/embedded-doc-solo.jsp">
    <jsp:param name="id" value="flowchart-doc"/>
    <jsp:param name="path" value="Event-Capacity-Flow.pdf"/>
    <jsp:param name="backupImg" value="Event-Capacity-Flow.png"/>
    <jsp:param name="description" value="Event Capacity Flowchart"/>
</jsp:include>