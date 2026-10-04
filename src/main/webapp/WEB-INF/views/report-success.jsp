<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setAttribute("pageTitle", "Report submitted"); %>
<%@ include file="includes/header.jspf" %>

<section class="container narrow fade-in">
    <div class="form-card wide">
        <p class="eyebrow accent">Report submitted</p>
        <h1>Thanks &mdash; it's on the board.</h1>
        <p class="muted">
            Your <c:out value="${fn:toLowerCase(item.type)}" /> report for
            <strong><c:out value="${item.itemName}" /></strong> is now visible to everyone on campus.
        </p>

        <c:choose>
            <c:when test="${not empty matches}">
                <h2 class="sub-head">We found ${fn:length(matches)} possible match${fn:length(matches) > 1 ? 'es' : ''}</h2>
                <%@ include file="includes/matches.jspf" %>
            </c:when>
            <c:otherwise>
                <p class="note">No matching reports yet. We'll show possible matches on your dashboard as soon as one appears.</p>
            </c:otherwise>
        </c:choose>

        <div class="actions">
            <a href="${ctx}/item?id=${item.id}" class="btn btn-dark">View report</a>
            <a href="${ctx}/dashboard" class="btn btn-outline">Go to dashboard</a>
        </div>
    </div>
</section>

<%@ include file="includes/footer.jspf" %>
