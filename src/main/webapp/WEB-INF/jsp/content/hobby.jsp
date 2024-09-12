<style>
    #cycling-font-colors .iframe {
        height: 200px;
        width: 640px !important;
    }

    #mtg {
        border-radius: 10px;
        width: 90%;
    }

    #mtg .iframe {
        height: 550px;
        width: 100%;
    }

    #mtg .screenshot {
        /* No backup screenshot; we'll just hide the frame when it doesn't fit */
        display: none !important;
    }

    #mtg-links {
        display: flex;
        flex-wrap: wrap;
        font-size: 18px;
        gap: 15px;
        height: 50px;
        justify-content: space-around;
    }

    #mtg-links * {
      align-self: center;
    }

    #steganos-example a:after {
        bottom: 8px;
        content: "Open GitHub";
    }

    #steganos-example a img {
        height: 183px;
        object-fit: cover;
        object-position: 0 1%;
    }

    @media screen and (min-width: 800px) {
        #cycling-font-colors {
            border-radius: 10px;
        }

        #cycling-font-colors .screenshot {
            display: none;
        }

        #cycling-font-colors .iframe {
            display: block;
        }
    }

    @media screen and (min-width: 1100px) {
        #mtg .iframe {
            display: block;
        }

        #mtg-links {
            height: 60px;
            margin-top: -10px;
        }
    }
</style>

<h1>Coding for Fun</h1>

<h2>Steganography</h2>
<p>
    My Java utility <b>Steganos</b> provides tools for simple text-in-image
    <jsp:include page="../widgets/link-out.jsp">
        <jsp:param name="text" value="steganography"/>
        <jsp:param name="href" value="https://en.wikipedia.org/wiki/Steganography"/>
    </jsp:include>
    by subtly encoding the bits of a message into the color channels of an image. This is useful for watermarking or
    passing messages in plain sight.
</p>

<jsp:include page="../widgets/see-it-on-gh.jsp">
    <jsp:param name="repo" value="steganos"/>
</jsp:include>

<jsp:include page="../widgets/project-specs.jsp">
    <jsp:param name="language" value="Java 11"/>
    <jsp:param name="versioning" value="GitHub"/>
    <jsp:param name="build" value="Maven"/>
    <jsp:param name="env" value="Command line or incorporate Core jar as a dependency"/>
</jsp:include>
<div id="steganos-example" class="screenshot solo">
    <a href="https://github.com/eric-eldard/steganos?tab=readme-ov-file#examples" target="_blank">
        <img src="https://github.com/eric-eldard/steganos/raw/master/examples.png?raw=true"
             alt="Link out to example output of Steganos on GitHub"
        >
    </a>
</div>

<hr>

<h2>MTG Player Aid</h2>
<p>
    I was hoping to get my fianc&eacute;e to try out the trading card game Magic: The Gathering, so I put this player
    aid together for her. It's just a simple html page. But as I wrote it, I got tired of constantly creating anchor
    tags to reference back to other parts of the aid. I ended up writing myself a little wiki-style linking library,
    allowing me to auto-link from tags like <span class="mono">[creature]</span> and
    <span class="mono">[Untap|tapping]</span>.
</p>
<div id="mtg-links">
    <jsp:include page="../widgets/link-out.jsp">
        <jsp:param name="text" value="Open in new tab"/>
        <jsp:param name="href" value="https://www.eric-eldard.com/mtg/"/>
    </jsp:include>
    <jsp:include page="../widgets/link-out.jsp">
        <jsp:param name="text" value="Check out the JSDoc"/>
        <jsp:param name="href" value="https://www.eric-eldard.com/mtg/docs/mtg.js.html"/>
    </jsp:include>
</div>
<jsp:include page="../widgets/embedded-iframe.jsp">
    <jsp:param name="id" value="mtg"/>
    <jsp:param name="description" value="MTG Player Aid"/>
    <jsp:param name="path" value="https://www.eric-eldard.com/mtg"/>
</jsp:include>

<hr>

<h2><span id="fontColorsTitle">Cycling Font Colors</span></h2>
<p>
    I saw this effect on another website and thought it would be a fun thing to build a tool for.<br>
    <span id="back2back1">To get the colors to</span>
    <span id="back2back2">fully cycle back and</span>
    <span id="back2back3">forth, put opposite</span>
    <span id="back2back4">strings back-to-back!</span>
</p>
<jsp:include page="../widgets/embedded-iframe.jsp">
    <jsp:param name="id" value="cycling-font-colors"/>
    <jsp:param name="description" value="Cycling Font Colors"/>
    <jsp:param name="path" value="Cycling_Font_Colors/index.html"/>
    <jsp:param name="backupImg" value="Cycling_Font_Colors.png"/>
</jsp:include>

<script>
    cycleFontColors("fontColorsTitle", 255, 0, 0, 0, 0, 255);
    cycleFontColors("back2back1", 0, 128, 255, 0, 192, 0);
    cycleFontColors("back2back2", 0, 192, 0, 0, 128, 255);
    cycleFontColors("back2back3", 0, 128, 255, 0, 192, 0);
    cycleFontColors("back2back4", 0, 192, 0, 0, 128, 255);
</script>