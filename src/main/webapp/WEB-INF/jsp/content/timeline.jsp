<div>
    <div class="timeline">
        <div class="timeline-events">
            <a href="javascript: retrieveAndShowContent('movie-theater')" class="timeline-event">&#x25cf;</a>
            <a href="javascript: retrieveAndShowContent('training-projects')" class="timeline-event">&#x25cf;</a>
            <a href="javascript: retrieveAndShowContent('software-engineer')" class="timeline-event">&#x25cf;</a>
            <a href="javascript: retrieveAndShowContent('solutions')" class="timeline-event">&#x25cf;</a>
            <a href="javascript: retrieveAndShowContent('executive')" class="timeline-event">&#x25cf;</a>
            <a href="javascript: retrieveAndShowContent('hobby')" class="timeline-event">&#x25cf;</a>
            <a href="javascript: retrieveAndShowContent('about-portfolio')" class="timeline-event">&#x25cf;</a>
        </div>
        <div class="timeline-dates">
            <span>2011</span>
            <span>&nbsp;</span>
            <span>2013</span>
            <span>&nbsp;</span>
            <span>2021</span>
            <span>&nbsp;</span>
            <span>2024</span>
        </div>
    </div>

    <style>
        .timeline .timeline-events.chrome-shim {
            bottom: -2px;
        }
    </style>

    <script>
        if (isChromeMobile()) {
            console.debug("Mobile Chrome detected; adding portfolio timeline CSS shim");
            document.querySelector(".timeline-events").classList.add("chrome-shim");
        }
    </script>
</div>