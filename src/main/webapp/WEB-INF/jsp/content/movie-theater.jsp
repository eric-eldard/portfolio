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
<jsp:include page="../widgets/embedded-iframe.jsp">
    <jsp:param name="id" value="mtc"/>
    <jsp:param name="description" value="Management Training Center"/>
    <jsp:param name="path" value="MTC/ManagementTrainingCenter.html"/>
    <jsp:param name="backupImg" value="MTC.png"/>
</jsp:include>
<p class="disclaimer">
    [Modules with proprietary info have been removed]
</p>

<hr>

<h2>Budget Buddy <span class="sm-heading">(Excel w/ VBA)</span></h2>
<p>
    Tracking an 8-figure movie theater budget is quite an undertaking&mdash;especially with no access to financial apps
    or even database software. Using Excel spreadsheets like database tables, I was able to create a Visual Basic
    application to track hundreds of expenditures, generate previously-handwritten reports, and help make financial
    projections. Leveraging available technology has always been one of my strengths.
</p>
<jsp:include page="../widgets/screenshot-solo.jsp">
    <jsp:param name="path"        value="Budget-Buddy.png"/>
    <jsp:param name="description" value="Budget Buddy (Excel w/ VBA)"/>
</jsp:include>

<hr>

<h2>Scheduling Software <span class="sm-heading">(Excel w/ VBA & XML)</span></h2>
<p>
    I saved dozens of person-hours each week by automating Excel paperwork with Visual Basic for Applications, allowing
    my management team to focus on serving customers and coaching staff. For example, I discovered our box office
    software published showtimes in a hidden XML feed to all of our back office computers. I used this feed to create
    various automated schedules (like movie-start and cleaning schedules) which had previously been made by hand. The
    solution was adopted by other theaters in our chain, since they all ran the same box office software.
</p>
<jsp:include page="../widgets/screenshot-solo.jsp">
    <jsp:param name="path"        value="usher-schedule.png"/>
    <jsp:param name="description" value="Usher Schedules (Excel w/ VBA)"/>
</jsp:include>