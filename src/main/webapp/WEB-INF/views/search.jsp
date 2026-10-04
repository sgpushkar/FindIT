<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setAttribute("pageTitle", "Search"); request.setAttribute("nav", "search"); %>
<%@ include file="includes/header.jspf" %>

<section class="container section fade-in">
    <p class="eyebrow">Search</p>
    <h1 class="page-title">Browse reports</h1>

    <form class="filters" action="${ctx}/search" method="get" role="search">
        <label class="span-2">Keyword
            <input type="text" name="q" value="${fn:escapeXml(q)}" placeholder="e.g. AirPods" maxlength="100">
        </label>
        <label>Type
            <select name="type">
                <option value="">Any</option>
                <option value="LOST" ${type == 'LOST' ? 'selected' : ''}>Lost</option>
                <option value="FOUND" ${type == 'FOUND' ? 'selected' : ''}>Found</option>
            </select>
        </label>
        <label>Category
            <select name="category">
                <option value="">Any</option>
                <c:forEach var="cat" items="${categories}">
                    <option value="${cat}" ${category == cat ? 'selected' : ''}><c:out value="${cat}" /></option>
                </c:forEach>
            </select>
        </label>
        <label>Location
            <input type="text" name="location" value="${fn:escapeXml(location)}" placeholder="e.g. Library" maxlength="100">
        </label>
        <label>Status
            <select name="status">
                <option value="">Any</option>
                <option value="ACTIVE" ${status == 'ACTIVE' ? 'selected' : ''}>Active</option>
                <option value="RESOLVED" ${status == 'RESOLVED' ? 'selected' : ''}>Resolved</option>
            </select>
        </label>
        <div class="filter-actions">
            <button type="submit" class="btn btn-dark">Search</button>
            <a href="${ctx}/search" class="btn btn-ghost">Clear</a>
        </div>
    </form>

    <p class="result-count">${fn:length(results)} result${fn:length(results) == 1 ? '' : 's'}</p>

    <c:choose>
        <c:when test="${empty results}">
            <div class="empty">
                <h3>No items found.</h3>
                <p>Nothing matching your search exists yet.</p>
                <a href="${ctx}/report" class="btn btn-dark">Report an Item</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="grid">
                <c:forEach var="item" items="${results}">
                    <%@ include file="includes/card.jspf" %>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</section>

<%@ include file="includes/footer.jspf" %>
