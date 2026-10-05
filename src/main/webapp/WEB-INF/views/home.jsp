<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setAttribute("pageTitle", "Home"); request.setAttribute("nav", "home"); %>
<%@ include file="includes/header.jspf" %>

<section class="hero container fade-in">
    <p class="eyebrow">Lost something?</p>
    <h1 class="hero-title">Someone might<br>have found it.</h1>
    <p class="hero-lead">Find what was lost. Return what was found.<br>The campus lost &amp; found, in one place.</p>
    <div class="hero-actions">
        <a href="${ctx}/report?type=LOST" class="btn btn-accent">Report Lost Item</a>
        <a href="${ctx}/report?type=FOUND" class="btn btn-outline">Report Found Item</a>
    </div>

    <form class="quick-search" action="${ctx}/search" method="get" role="search">
        <input type="text" name="q" placeholder="Search by item, category or location..." aria-label="Search" maxlength="100">
        <button type="submit" class="btn btn-dark">Search</button>
    </form>

    <dl class="stats">
        <div><dt>Lost reports</dt><dd>${activeLost}</dd></div>
        <div><dt>Found reports</dt><dd>${activeFound}</dd></div>
        <div><dt>Returned</dt><dd>${resolvedCount}</dd></div>
    </dl>
</section>

<section class="container section fade-in">
    <div class="section-head">
        <h2>Recent reports</h2>
        <a href="${ctx}/search" class="link">View all &rarr;</a>
    </div>
    <c:choose>
        <c:when test="${empty recentItems}">
            <div class="empty">
                <h3>Nothing reported yet.</h3>
                <p>Be the first to report a lost or found item.</p>
                <a href="${ctx}/report" class="btn btn-dark">Report an Item</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="grid">
                <c:forEach var="item" items="${recentItems}">
                    <%@ include file="includes/card.jspf" %>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</section>

<section class="container section how fade-in">
    <h2>How it works</h2>
    <ol class="steps">
        <li><span>01</span><h3>Report</h3><p>Tell us what you lost or found, where and when.</p></li>
        <li><span>02</span><h3>Match</h3><p>FindIT compares reports and suggests possible matches.</p></li>
        <li><span>03</span><h3>Return</h3><p>Meet up, hand it back, and mark the report resolved.</p></li>
    </ol>
</section>

<%@ include file="includes/footer.jspf" %>
