<style>
    #flowchart-doc {
        aspect-ratio: 128 / 69;
        width: 95%;
    }

    #dfd-doc {
        height: 575px;
    }

    #partner-doc {
        height: 575px;
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
    would integrate into the rest of the stack over the 2 years.
</p>

<h2>Brazen Partner Integration</h2>
<p>
    In 2022, Brazen worked with a major job board to use Brazen Virtual Events as the interview component of their
    hiring funnel. After consulting with their product and tech folks on the need, I put together this interactive
    sequence diagram, illustrating the fastest path to achieving this integration. This really helped to advance our
    conversation. I know reading someone else's plan daunting, which is why I believe in clean, attractive visuals.
</p>
<p class="disclaimer">
    [Partner information anonymized]
</p>
<div id="partner-doc" class="embedded-doc">
    <iframe src="/portfolio/assets/documents/Partner-Integration.pdf#navpanes=0&view=FitH" allowfullscreen></iframe>
</div>

<hr>

<h2>developers.brazen.com</h2>
<p>
    <jsp:include page="../widgets/link-out.jsp">
        <jsp:param name="url" value="https://developers.brazen.com/"/>
        <jsp:param name="text" value="Brazen Developer Resources"/>
    </jsp:include>
    advertises all the ways our customers and partners could integrate with us. This portal was created and maintained
    by me and one of my long-time Brazen collaborators. It showcases some solutions I helped build from the ground-up,
    like our customer-facing RESTful API, our XML job feeds, and our embeddable chat plugin. I also staffed
    developers@brazen.com, assisting our customers' tech folks as they built their integrations. I love building tools
    for other devs to build cool stuff!
</p>
<jsp:include page="../widgets/solo-screenshot.jsp" >
    <jsp:param name="path" value="/portfolio/assets/images/screenshots/brazen-dev-resources.png"/>
    <jsp:param name="altText" value="Brazen Developer Resources screenshot"/>
</jsp:include>

<hr>

<h2>Brazen Data Flows</h2>
<p>
    When Brazen joined the Radancy family, I put this data flow diagram together to help our new teammates understand
    what kind of data we worked with and how it was enriched as it moved through our system&mdash;and ultimately which
    points in the Brazen candidate journey would provide the highest value for integration. I produced both this YC
    version and a multi-level GC version, which can be easier to digest for folks unfamiliar with your system.
</p>
<div id="dfd-doc" class="embedded-doc">
    <iframe src="/portfolio/assets/documents/Candidate-and-Job-Data-Flows.pdf#navpanes=0&view=FitH" allowfullscreen></iframe>
</div>

<hr>

<h2>Brazen Event Capacity Flow</h2>
<p>
    I put this flowchart together for a customer who was looking to better understand the interplay of several advanced
    features of our event sign-up flow. The use of screenshots here helps tie abstract concepts to our more familiar ui.
</p>
<div id="flowchart-doc" class="embedded-doc">
    <iframe src="/portfolio/assets/documents/Event-Capacity-Flow.pdf#navpanes=0&view=FitH" allowfullscreen></iframe>
</div>