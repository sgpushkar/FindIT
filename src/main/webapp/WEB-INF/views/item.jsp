<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setAttribute("pageTitle", "Report details"); %>
<%@ include file="includes/header.jspf" %>

<section class="container narrow fade-in">
    <a href="${ctx}/search" class="back">&larr; Back to search</a>

    <article class="detail">
        <div class="card-top">
            <span class="tag ${item.lost ? 'tag-lost' : 'tag-found'}"><c:out value="${item.type}" /></span>
            <span class="status ${item.resolved ? 'status-resolved' : 'status-active'}"><c:out value="${item.status}" /></span>
        </div>
        <h1 class="detail-title"><c:out value="${item.itemName}" /></h1>

        <dl class="detail-grid">
            <div><dt>Category</dt><dd><c:out value="${item.category}" /></dd></div>
            <div><dt>Location</dt><dd><c:out value="${item.location}" /></dd></div>
            <div><dt>Date</dt><dd><c:out value="${item.longDate}" /></dd></div>
            <div><dt>Reported by</dt><dd><c:out value="${item.reporterName}" /></dd></div>
        </dl>

        <div class="detail-desc">
            <h2>Description</h2>
            <p><c:out value="${empty item.description ? 'No description provided.' : item.description}" /></p>
        </div>

        <c:if test="${canManage && !item.resolved}">
            <form action="${ctx}/resolve" method="post" class="detail-action"
                  onsubmit="return confirm('Mark this report as resolved?');">
                <input type="hidden" name="id" value="${item.id}">
                <button type="submit" class="btn btn-accent">Mark as Resolved</button>
                <span class="muted small">Use this once the item is back with its owner.</span>
            </form>
        </c:if>
        <c:if test="${item.resolved}">
            <p class="note">This item has been returned. The report is kept for reference.</p>
        </c:if>
        <c:if test="${empty sessionScope.user && !item.resolved}">
            <p class="note">Is this yours, or did you find it? <a class="link" href="${ctx}/login">Log in</a> to report or manage items.</p>
        </c:if>
    </article>

    <c:if test="${not empty matches}">
        <h2 class="sub-head">Possible matches</h2>
        <%@ include file="includes/matches.jspf" %>
    </c:if>
</section>

<%@ include file="includes/footer.jspf" %>
