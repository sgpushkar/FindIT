<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%
    request.setAttribute("pageTitle", "Report an item");
    request.setAttribute("nav", "FOUND".equals(request.getAttribute("type")) ? "found" : "lost");
%>
<%@ include file="includes/header.jspf" %>

<section class="container narrow fade-in">
    <div class="form-card wide">
        <p class="eyebrow">New report</p>
        <h1>Report an item</h1>
        <p class="muted">Clear details help the right person find it faster.</p>

        <c:if test="${not empty error}">
            <div class="alert"><c:out value="${error}" /></div>
        </c:if>

        <form action="${ctx}/report" method="post" class="form" novalidate>
            <fieldset class="type-choice">
                <legend>What happened?</legend>
                <label class="choice">
                    <input type="radio" name="type" value="LOST" ${type == 'LOST' ? 'checked' : ''}>
                    <span><strong>I lost something</strong><small>Ask the campus for help</small></span>
                </label>
                <label class="choice">
                    <input type="radio" name="type" value="FOUND" ${type == 'FOUND' ? 'checked' : ''}>
                    <span><strong>I found something</strong><small>Help it get back home</small></span>
                </label>
            </fieldset>

            <label>Item name
                <input type="text" name="itemName" value="${fn:escapeXml(form.itemName)}" placeholder="e.g. Black Wallet" required maxlength="100">
            </label>

            <div class="form-row">
                <label>Category
                    <select name="category" required>
                        <option value="">Choose a category</option>
                        <c:forEach var="cat" items="${categories}">
                            <option value="${cat}" ${form.category == cat ? 'selected' : ''}><c:out value="${cat}" /></option>
                        </c:forEach>
                    </select>
                </label>
                <label>Date
                    <input type="date" name="itemDate" value="${empty form.itemDate ? today : fn:escapeXml(form.itemDate)}" max="${today}" required>
                </label>
            </div>

            <label>Location
                <input type="text" name="location" value="${fn:escapeXml(form.location)}" placeholder="e.g. Library, Canteen, Main Gate" required maxlength="100">
            </label>

            <label>Description
                <textarea name="description" rows="4" maxlength="1000" placeholder="Colour, brand, distinguishing marks...">${fn:escapeXml(form.description)}</textarea>
            </label>

            <button type="submit" class="btn btn-accent btn-block">Submit report</button>
        </form>
    </div>
</section>

<%@ include file="includes/footer.jspf" %>
