<a href="${param.url}"
   target="_blank"
   class="link-out"
<%-- displacement math not rendering correctly in Firefox, so this has been temporarily commented out
   style="
        ${param.right ne null ? '--right_displacement: '.concat(param.right.concat('px;')) : ''}
        ${param.top ne null ? '--top_displacement: '.concat(param.top.concat('px;')) : ''}
   "
--%>
>${param.text}</a>