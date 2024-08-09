<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<div class="authorization-container">
    <div id="forever-auth-${user.id}" ${user.authorizedUntil == null ? "" : "style='display: none'"}>
        <i>forever</i>
        <a href="javascript: void(0)"
           onclick="javascript:
                    document.getElementById('forever-auth-${user.id}').style.display='none';
                    document.getElementById('exp-date-${user.id}').style.display='inline-block'"
           class="emoji-silhouette clock"
           title="Set authorization end date"
        >
            &#9202;
        </a>
    </div>
    <div id="exp-date-${user.id}" ${user.authorizedUntil == null ? "style='display: none'" : ""}>
        <fmt:formatDate pattern="yyyy-MM-dd" value="${user.authorizedUntil}" var="authorizedUntilFormatted"/>
        <input
                type="date"
                onchange="setAuthUntil(${user.id}, '${user.username}', this.value)"
                ${user.isAccountNonExpired() ? "" : "class='expired-account'"}
                ${user.authorizedUntil == null ? "" : "value='".concat(authorizedUntilFormatted).concat("'")}
        />
        <a href="javascript: setInfiniteAuth(${user.id}, '${user.username}')" class="infinity" title="Authorize forever">
            &infin;
        </a>
    </div>
</div>