<%@ page session="false" %>
<style>
    #cycling-font-colors .iframe {
        height: 200px;
        width: 640px !important;
    }

    #harpocrates-example,
    .steganos-example {
        max-width: 850px;
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
        /* No backup screenshot; we'll just hide the frame when the content doesn't fit */
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

    .junie-voice-example {
        max-width: 740px;
    }

    .junie-voice-example a:after,
    .steganos-example a:after {
        content: "Open GitHub";
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

<h2>Junie Voice</h2>
<p>
    As a Java dev, JetBrains' Junie (wrapping Claude 4) has become my coding agent of choice, since it works where I do.
    But it seems voice integration isn't on its roadmap and I've found it really useful to bounce ideas off of ChatGPT
    voice mode. However, ChatGPT doesn't have to my code base, and OpenAI's Codex doesn't have a voice mode.
</p>
<p>
    <b>Junie Voice</b> is an IntelliJ plugin to bridge this gap, allowing voice chatting right inside the IDE. It adds
    features I've always wanted from ChatGPT, like the ability for the agent to speak one thing, but print something
    different. This eliminates the voice agent speaking code aloud, while still providing that code as text. More
    broadly, Junie Voice allows for your choice of text, voice, or image input, then keeps the conversation going while
    outputting relevant snippets. When you're ready to build, Junie Voice passes the spec you've created to Junie to
    make the actual changes in your project.
</p>
<jsp:include page="../widgets/see-it-on-gh.jsp">
    <jsp:param name="repo" value="junie-voice"/>
</jsp:include>

<jsp:include page="../widgets/project-specs.jsp">
    <jsp:param name="ai"         value="gpt-4.1-mini (text), gpt-4o-mini-realtime-preview (voice)"/>
    <jsp:param name="language"   value="Java 21, IntelliJ PSI SDK"/>
    <jsp:param name="versioning" value="GitHub"/>
    <jsp:param name="build"      value="Gradle"/>
    <jsp:param name="deployment" value="IntelliJ plugin"/>
</jsp:include>

<jsp:include page="../widgets/screenshot-solo.jsp">
    <jsp:param name="path"        value="junie-voice.png"/>
    <jsp:param name="href"        value="https://github.com/eric-eldard/junie-voice?tab=readme-ov-file#junie-voice"/>
    <jsp:param name="description" value="Junie Voice"/>
    <jsp:param name="classes"     value="junie-voice-example"/>
</jsp:include>

<hr>

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
    <jsp:param name="language"   value="Java 11"/>
    <jsp:param name="versioning" value="GitHub"/>
    <jsp:param name="build"      value="Maven"/>
</jsp:include>

<jsp:include page="../widgets/screenshot-solo.jsp">
    <jsp:param name="path"        value="steganos.png"/>
    <jsp:param name="href"        value="https://github.com/eric-eldard/steganos?tab=readme-ov-file#examples"/>
    <jsp:param name="description" value="Steganos output examples"/>
    <jsp:param name="classes"     value="steganos-example"/>
</jsp:include>

<hr>

<h2>Data Classifier & Obfuscator</h2>
<p>
    My <b>Harpocrates Annotation</b> and <b>Persistence</b> libraries allow developers to annotate sensitive data
    fields, classifying the type of data they represent; these classifications are copied to your database columns
    when your app starts up. At any time, pipe a classified database dump through the <b>Harpocrates Obfuscator</b>
    app to create a sanitized test dataset, with smart replacements for all kinds of sensitive data, including names,
    email addresses, phone numbers, and much more!
</p>
<p>
    As an added bonus, your data classifications live with both your code <i>and</i> your data&mdash;useful if other
    applications have access to your data!
</p>

<jsp:include page="../widgets/see-it-on-gh.jsp">
    <jsp:param name="repo" value="harpocrates"/>
</jsp:include>

<jsp:include page="../widgets/project-specs.jsp">
    <jsp:param name="language"   value="Java 21, Spring 6"/>
    <jsp:param name="versioning" value="GitHub"/>
    <jsp:param name="build"      value="Maven"/>
</jsp:include>

<div id="harpocrates-example" class="java-code"><span class="annotation unhighlight">@Entity</span>
<span class="unhighlight"><span class="keyword">public class</span> User</span>
<span class="unhighlight">{</span>
    <span class="unhighlight"><span class="annotation">@Column</span>(name = <span class="string">"firstName"</span>)</span>
    <span class="highlight"><span class="annotation">@DataClassification</span>(DataType.<span class="member">GIVEN_NAME</span>)</span>
    <span class="unhighlight"><span class="keyword">private</span> String <span class="member">givenName</span><span class="keyword">;</span></span>

    <span class="unhighlight"><span class="annotation">@Column</span>(name = <span class="string">"lastName"</span>)</span>
    <span class="highlight"><span class="annotation">@DataClassification</span>(DataType.<span class="member">SURNAME</span>)</span>
    <span class="unhighlight"><span class="keyword">private</span> String <span class="member">surname</span><span class="keyword">;</span></span>

    <span class="highlight"><span class="annotation">@DataClassification</span>(type = DataType.<span class="member">EMAIL_ADDRESS</span>, pattern = <span class="string">"{SURNAME}.{GIVEN_NAME}@my-company.com"</span>)</span>
    <span class="unhighlight"><span class="keyword">private</span> String <span class="member">email</span><span class="keyword">;</span></span>

    <span class="highlight"><span class="annotation">@DataClassification</span>(type = DataType.<span class="member">PHONE_NUMBER</span>, action = Action.<span class="member">REMOVE</span>)</span>
    <span class="unhighlight"><span class="keyword">private</span> String <span class="member">phoneNumber</span><span class="keyword">;</span></span>
<span class="unhighlight">}</span>
</div>

<hr>

<h2>MTG Player Aid</h2>
<p>
    I was hoping to get my fianc&eacute;e to try out the trading card game Magic: The Gathering, so I put this player
    aid together for her. It's just a simple html page. But as I wrote it, I got tired of constantly creating anchor
    tags to reference back to other parts of the aid. I ended up writing myself a little wiki-style linking library,
    allowing me to auto-link from tags like <span class="mono">[creature]</span> and
    <span class="mono">[Untap|tapping]</span>, as well as auto-generate a table of contents and quick-reference index.
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
    <jsp:param name="id"          value="mtg"/>
    <jsp:param name="description" value="MTG Player Aid"/>
    <jsp:param name="path"        value="https://www.eric-eldard.com/mtg"/>
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
    <jsp:param name="id"          value="cycling-font-colors"/>
    <jsp:param name="description" value="Cycling Font Colors"/>
    <jsp:param name="path"        value="Cycling_Font_Colors/index.html"/>
    <jsp:param name="backupImg"   value="Cycling_Font_Colors.png"/>
</jsp:include>

<script>
    Portfolio.cycleFontColors("fontColorsTitle", 255, 0, 0, 0, 0, 255);
    Portfolio.cycleFontColors("back2back1", 0, 128, 255, 0, 192, 0);
    Portfolio.cycleFontColors("back2back2", 0, 192, 0, 0, 128, 255);
    Portfolio.cycleFontColors("back2back3", 0, 128, 255, 0, 192, 0);
    Portfolio.cycleFontColors("back2back4", 0, 192, 0, 0, 128, 255);
</script>