<style>
    #mtc {
        margin-left: auto;
        margin-right: auto;
        overflow-x: scroll;
    }

    #mtc .iframe {
        height: 785px;
        width: 1045px;
    }

    #mtc .iframe iframe {
        height: 100%;
        width: 100%;
    }
</style>

<h1>Coding at the Movies</h1>
<h2>Management Training Center <span class="sm-heading">(iframes, CSS, JavaScript)</span></h2>
<p>
    I was running a movie theater in 2011 when my company dissolved its management training program with no replacement.
    Coding was well beyond the scope of my responsibilities when I managed theaters, but I created this training portal
    for my team. I continued to roll out new modules until I left the company.
</p>
<p class="disclaimer">
    [One module with no proprietary info is shown below]
</p>
<div id="mtc">
    <div class="iframe">
        <iframe src="/portfolio/assets/projects/MTC/ManagementTrainingCenter.html"></iframe>
    </div>
</div>

<hr>

<h2>Budget Buddy <span class="sm-heading">(Excel w/ VBA)</span></h2>
<p>
    Tracking an 8-figure movie theater budget with expenditures ranging from thousands of dollars down to a couple bucks
    is quite an undertaking, especially with no access to financial apps or even database software. Excel with VBA to
    the rescue! Using spreadsheet tables like database tables, I was able to create an application that tracked hundreds
    of expenditures, generated previously-handwritten reports, and helped make projections. Leveraging available
    technology has always been one of my strengths.
</p>
<jsp:include page="../widgets/solo-screenshot.jsp">
    <jsp:param name="path" value="/portfolio/assets/images/screenshots/Budget_Buddy.png"/>
    <jsp:param name="altText" value="Budget Buddy (Excel w/ VBA) screenshot"/>
</jsp:include>

<hr>

<h2>Scheduling Software <span class="sm-heading">(Excel w/ VBA & XML)</span></h2>
<p>
    I was able to save dozens of person-hours each week by automating Excel paperwork with Visual Basic for
    Applications. Here's an example in which I found out our box office software was publishing our showtimes in XML to
    potentially be used by electronic signage software. I was able to consume this and create all kinds of schedule
    variants (movie start schedules for projectionists, cleaning schedules for ushers, etc.) which previously had to be
    created by hand. Usage spread to other theaters in our chain, who could all use it too since they all ran the same
    box office software.
</p>
<p>
    Beyond consuming showtimes, I was also working on creating weekly show schedules. Normally done by hand, these have
    to follow a lot of rules for when movies can play, including film ratings, target audiences, number of copies you
    have, weeks each movie has been out, each building's operating hours, and much more. With all of these dimensions,
    I was in the midst of studying chess AI when I left the theater business, in hopes to one day get our weekly
    showtime schedule to write itself.
</p>
<jsp:include page="../widgets/solo-screenshot.jsp" >
    <jsp:param name="path" value="/portfolio/assets/images/screenshots/Usher_Schedule.png"/>
    <jsp:param name="altText" value="Usher Schedules (Excel w/ VBA) screenshot"/>
</jsp:include>