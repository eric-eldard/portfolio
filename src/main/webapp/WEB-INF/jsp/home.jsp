<%@ page session="false" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <jsp:include page="widgets/headers.jsp"/>
        <meta name="robots" content="nofollow">
        <link rel="stylesheet" type="text/css" href="/public/assets/style/home.css">
    </head>
    <body>
        <div id="main">
            <jsp:include page="content/title-subtitle.jsp"/>

            <div class="content">
                <div class="intro-heading">What can we build together?</div>
                <jsp:include page="content/intro.jsp"/>

                <div class="link-container">
                    <a href="portfolio/" class="link-card have-password">
                        Already have a password?
                        <br>
                        <b>Sign in to see my work!</b>
                    </a>

                    <a href="https://www.linkedin.com/in/eric-eldard" target="_blank" class="link-card connect-li">
                        Don't have a  password?
                        <br>
                        <b>
                            Connect with me on
                            <span class="linkedin">
                                <img src="/public/assets/images/third-party/linkedin.png" alt="LinkedIn">
                            </span>
                        </b>
                    </a>
                </div>
            </div>

        </div>
    </body>
</html>