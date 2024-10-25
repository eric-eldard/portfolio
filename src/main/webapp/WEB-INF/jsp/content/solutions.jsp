<%@ page session="false" %>
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
    This is a role I would play a good deal as Brazen became part of the Radancy product suite, and we designed how
    Brazen's product would integrate into the rest of the stack over the first 2 years.
</p>

<h2>Brazen Partner Integration</h2>
<p>
    In 2022, Brazen worked with a major job board to use Brazen Virtual Events as the interview component of their
    hiring funnel. After consulting with their stakeholders, I put together this interactive sequence diagram,
    illustrating the fastest path to achieving this integration. This really helped to advance our conversation.
</p>
<jsp:include page="../widgets/embedded-doc-solo.jsp">
    <jsp:param name="id"          value="partner-doc"/>
    <jsp:param name="path"        value="Partner-Integration.pdf"/>
    <jsp:param name="backupImg"   value="Partner-Integration.png"/>
    <jsp:param name="description" value="Partner Integration Sequence Diagram"/>
</jsp:include>
<p class="disclaimer">
    [Partner info anonymized]
</p>

<hr>

<h2>developers.brazen.com</h2>
<p>
    The
    <jsp:include page="../widgets/link-out.jsp">
        <jsp:param name="text" value="Brazen Developer Portal"/>
        <jsp:param name="href" value="https://developers.brazen.com/"/>
    </jsp:include>
    advertises all the ways customers and partners can integrate with Brazen. This portal was created and maintained by
    me and one of my long-time Brazen collaborators. It showcases many solutions I helped design and build from the
    ground-up, like our customer-facing RESTful API, our XML job feeds, and our embeddable chat plugin. I also staffed
    developers@brazen.com, assisting our customers' tech folks as they built their integrations.
</p>
<jsp:include page="../widgets/screenshot-solo.jsp">
    <jsp:param name="path"        value="brazen-dev-resources.png"/>
    <jsp:param name="description" value="Brazen Developer Portal"/>
    <jsp:param name="href"        value="https://developers.brazen.com/"/>
    <jsp:param name="classes"     value="new-tab"/>
</jsp:include>

<hr>

<h2>Brazen Data Flows</h2>
<p>
    When Brazen joined the Radancy family, I put this data flow diagram together to help our new teammates understand
    what kind of data we worked with and how it was enriched as it moved through our system&mdash;and ultimately which
    points in the Brazen candidate journey would provide the highest value for integration.
</p>
<jsp:include page="../widgets/embedded-doc-solo.jsp">
    <jsp:param name="id"          value="dfd-doc"/>
    <jsp:param name="path"        value="Candidate-and-Job-Data-Flows.pdf"/>
    <jsp:param name="backupImg"   value="Candidate-and-Job-Data-Flows.png"/>
    <jsp:param name="description" value="Candidate and Job Data Flow Diagram"/>
</jsp:include>