<style>
    #cycling-font-colors {
        overflow-x: scroll;
    }

    #cycling-font-colors .iframe {
        height: 200px;
        width: 600px !important;
    }

    #cycling-font-colors .iframe iframe {
        height: 100%;
        width: 100%;
    }

    #mtg .iframe {
        height: 550px;
        width: 90% !important;
    }

    #mtg .iframe iframe {
        height: 100%;
        width: 100%;
    }
</style>

<h1>Coding for Fun</h1>

<h2>Steganography</h2>
<p>
    My Java utility <b>steganos</b> provides tools for simple text-in-image steganography by subtly encoding the bits of a message into the color channels of an image. This is useful for watermarking or passing messages in plain sight.
</p>

<jsp:include page="../widgets/see-it-on-gh.jsp">
    <jsp:param name="repo" value="steganos"/>
</jsp:include>

<jsp:include page="../widgets/project-specs.jsp">
    <jsp:param name="language" value="Java 11"/>
    <jsp:param name="versioning" value="GitHub"/>
    <jsp:param name="build" value="Maven"/>
    <jsp:param name="env" value="Command line"/>
</jsp:include>
<div>
    <a href="https://github.com/eric-eldard/steganos?tab=readme-ov-file#examples" target="_blank">
        <img class="solo" style="height: 200px; object-fit: cover; object-position: 0 1%;" src="https://github.com/eric-eldard/steganos/raw/master/examples.png?raw=true">
    </a>
</div>

<hr>

<h2>MTG Player Aid</h2>
<p>
    I was hoping to get my fianc&eacute;e to try out the trading card game Magic: The Gathering, so I put this player aid together for her. It's just a simple html page. But as I wrote it, I got tired of constantly creating anchor tags to reference back to other parts of the aid. I ended up writing myself a little wiki-style linking library, allowing me to auto-link from tags like <span class="mono">[creature]</span> and <span class="mono">[Untap|tapping]</span>.
</p>
<p>
    <a href="https://www.eric-eldard.com/mtg/docs/mtg.js.html" target="_blank" class="link-out">
        Check out the source
    </a>
</p>
<div id="mtg">
    <div class="iframe">
        <iframe src="https://www.eric-eldard.com/mtg/"></iframe>
    </div>
</div>

<hr>

<h2><span id="fontColorsTitle">Cycling Font Colors</span></h2>
<p>
    I saw this effect on another website and thought it would be a fun thing to build a tool for.<br>
    <span id="back2back1">To get the colors to </span><span id="back2back2">fully cycle back and </span><span id="back2back3">forth, put opposite </span><span id="back2back4">strings back-to-back.</span>
</p>
<div id="cycling-font-colors">
    <div class="iframe">
        <iframe src="/portfolio/assets/projects/Cycling_Font_Colors/index.html"></iframe>
    </div>
</div>

<c:set var="red" value="${red}"/>
<c:set var="green" value="${green}"/>
<c:set var="blue" value="${blue}"/>

<script>
    cycleFontColors("fontColorsTitle", 255, 0, 0, 0, 0, 255);
    cycleFontColors("back2back1", 0, 128, 255, 0, 192, 0);
    cycleFontColors("back2back2", 0, 192, 0, 0, 128, 255);
    cycleFontColors("back2back3", 0, 128, 255, 0, 192, 0);
    cycleFontColors("back2back4", 0, 192, 0, 0, 128, 255);
</script>