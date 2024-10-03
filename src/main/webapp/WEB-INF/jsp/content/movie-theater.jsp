<style>
    #mtc .iframe {
        height: 765px;
        width: 1040px;
    }

    @media screen and (min-width: 1300px) {
        #mtc {
            border-radius: 10px;
        }

        #mtc .iframe {
            display: block;
        }

        #mtc .screenshot {
            display: none;
        }
    }
</style>

<h1>Coding at the Movies</h1>
<h2>Management Training&nbsp;Center <span class="sm-heading">(iframes, CSS, JavaScript)</span></h2>
<p>
    I was running a movie theater in 2011 when my company dissolved its management training program with no replacement.
    Coding was well beyond the scope of my responsibilities, but I created this training portal for my team. I continued
    to roll out new modules until I left the company.
</p>
<p class="disclaimer">
    [Modules with proprietary info removed]
</p>
<jsp:include page="../widgets/embedded-iframe.jsp">
    <jsp:param name="id" value="mtc"/>
    <jsp:param name="description" value="Management Training Center"/>
    <jsp:param name="path" value="MTC/ManagementTrainingCenter.html"/>
    <jsp:param name="backupImg" value="MTC.png"/>
</jsp:include>

<hr>

<h2>Budget Buddy <span class="sm-heading">(Excel w/ VBA)</span></h2>
<p>
    Tracking an 8-figure movie theater budget, with expenditures ranging from thousands of dollars down to a couple
    bucks, is quite an undertaking&mdash;especially with no access to financial apps or even database software. Excel
    with VBA to the rescue! Using spreadsheet tables like database tables, I was able to create a Visual Basic
    application to track hundreds of expenditures, generate previously-handwritten reports, and help make
    financial projections. Leveraging available technology has always been one of my strengths.
</p>
<jsp:include page="../widgets/screenshot-solo.jsp">
    <jsp:param name="path"        value="Budget-Buddy.png"/>
    <jsp:param name="description" value="Budget Buddy (Excel w/ VBA)"/>
</jsp:include>

<hr>

<h2>Scheduling Software <span class="sm-heading">(Excel w/ VBA & XML)</span></h2>
<p>
    I was able to save dozens of person-hours each week by automating Excel paperwork with Visual Basic for
    Applications. This got my management team out of the office and onto the floor, helping customers and coaching
    staff. Here's an example in which I found out our box office software was publishing our showtimes in an XML feed
    to a hidden folder on each of our back office computers, just in case one of them happened to be running electronic
    signage software. I was able to consume this feed and create all kinds of schedule variants (movie-start schedules
    for projectionists, cleaning schedules for ushers, etc.) which previously had to be created by hand. Usage spread
    to other theaters in our chain, who could all use it too since they all ran the same box office software.
</p>
<p>
    Beyond consuming showtimes, I was also working on creating weekly show schedules. Normally done by hand, these have
    to follow a lot of criteria for when movies can play, including film ratings, target audiences, number of copies you
    have, your operating hours, and much more. With all of these dimensions, I was in the midst of studying chess AI
    when I left the theater business, in hopes to one day get our weekly showtime schedule to write itself.
</p>
<jsp:include page="../widgets/screenshot-solo.jsp">
    <jsp:param name="path"        value="usher-schedule.png"/>
    <jsp:param name="description" value="Usher Schedules (Excel w/ VBA)"/>
</jsp:include>