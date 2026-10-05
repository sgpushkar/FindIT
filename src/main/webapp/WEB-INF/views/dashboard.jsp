<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setAttribute("pageTitle", "Dashboard"); request.setAttribute("nav", "dashboard"); %>
<%@ include file="includes/header.jspf" %>

<section class="container section fade-in">
    <div class="section-head">
        <div>
            <p class="eyebrow">Dashboard</p>
            <h1 class="page-title">Welcome, <c:out value="${sessionScope.user.firstName}" /></h1>
        </div>
        <a href="${ctx}/report" class="btn btn-accent">Report an Item</a>
    </div>

    <c:if test="${not empty matches}">
        <h2 class="sub-head">Possible matches for your reports</h2>
        <%@ include file="includes/matches.jspf" %>
    </c:if>

    <div class="section-head mt">
        <h2>My reports</h2>
    </div>
    <nav class="tabs" aria-label="Filter reports">
        <c:forEach var="f" items="${filters}">
            <a href="${ctx}/dashboard?filter=${f}" class="tab ${filter == f ? 'active' : ''}">
                ${f == 'ALL' ? 'All' : fn:substring(f, 0, 1)}${f == 'ALL' ? '' : fn:toLowerCase(fn:substring(f, 1, fn:length(f)))}
            </a>
        </c:forEach>
    </nav>

    <c:choose>
        <c:when test="${empty items}">
            <div class="empty">
                <c:choose>
                    <c:when test="${filter == 'ALL'}">
                        <h3>You haven't reported anything yet.</h3>
                        <p>Lost something, or found something that isn't yours?</p>
                    </c:when>
                    <c:otherwise>
                        <h3>No reports in this view.</h3>
                        <p>Try another filter.</p>
                    </c:otherwise>
                </c:choose>
                <a href="${ctx}/report" class="btn btn-dark">Report an Item</a>
            </div>
        </c:when>
        <c:otherwise>
            <ul class="report-list">
                <c:forEach var="item" items="${items}">
                    <li>
                        <a href="${ctx}/item?id=${item.id}" class="report-row">
                            <span class="tag ${item.lost ? 'tag-lost' : 'tag-found'}"><c:out value="${item.type}" /></span>
                            <span class="report-name"><c:out value="${item.itemName}" /></span>
                            <span class="report-loc"><c:out value="${item.location}" /></span>
                            <span class="report-date"><c:out value="${item.displayDate}" /></span>
                            <span class="status ${item.resolved ? 'status-resolved' : 'status-active'}"><c:out value="${item.status}" /></span>
                        </a>
                    </li>
                </c:forEach>
            </ul>
        </c:otherwise>
    </c:choose>
</section>

<%@ include file="includes/footer.jspf" %>
