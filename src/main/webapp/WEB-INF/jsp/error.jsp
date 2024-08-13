<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <jsp:include page="widgets/no-viewport-zoom.jsp"/>
        <jsp:include page="widgets/headers.jsp"/>

        <style>
            a {
            color: #ddf;
                text-decoration: none;
                transition: color 0.75s;
            }

            a:hover {
                color: #ffd;
            }

            body {
                background-color: #000;
                color: #fff;
                font-family: Helvetica, sans-serif;
                font-size: 20px;
            }

            #main {
                margin-left: auto;
                margin-right: auto;
                padding: 15px;
            }

            .heading {
                font-size: 60px;
                margin-bottom: 25px;
                margin-left: -4px;
            }

            @media screen and (min-width: 600px) {
                #main {
                    /* creates a meaty area mid-screen for the messages, but not on mobile where the text needs to wrap */
                    width: 600px;
                }

                .heading {
                    font-size: 120px;
                }
            }
        </style>
    </head>

    <body>

        <div id="main">
            <div class="heading">HTTP ${status}</div>
            <p>
                <c:choose>
                    <c:when test="${status == 401}">Hmmmmm, we don't seem to recognize you.</c:when>
                    <c:when test="${status == 403}">Hmmmmm, you don't seem to have access to that.</c:when>
                    <c:when test="${status == 404}">You got lost on a site with only one page?</c:when>
                    <c:otherwise>Hmmmmm, we're not sure what happened.</c:otherwise>
                </c:choose>
            </p>
            <p>
                <a href="/">Let's get you back to the portfolio</a>
            </p>
        </div>
    </body>
</html>