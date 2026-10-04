<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setAttribute("pageTitle", "Log in"); request.setAttribute("nav", "login"); %>
<%@ include file="includes/header.jspf" %>

<section class="container narrow fade-in">
    <div class="form-card">
        <p class="eyebrow">Welcome back</p>
        <h1>Log in</h1>
        <p class="muted">Use your FindIT account to report and manage items.</p>

        <c:if test="${not empty error}">
            <div class="alert"><c:out value="${error}" /></div>
        </c:if>

        <form action="${ctx}/login" method="post" class="form" novalidate>
            <input type="hidden" name="next" value="${fn:escapeXml(next)}">
            <label>Email
                <input type="email" name="email" value="${fn:escapeXml(email)}" required maxlength="255" autocomplete="email">
            </label>
            <label>Password
                <input type="password" name="password" required autocomplete="current-password">
            </label>
            <button type="submit" class="btn btn-dark btn-block">Log in</button>
        </form>

        <p class="form-foot">New here? <a href="${ctx}/register" class="link">Create an account</a></p>

        <div class="demo-note">
            <strong>Demo accounts (college project only)</strong>
            <span>Admin &mdash; admin@findit.local / admin123</span>
            <span>Student &mdash; student@findit.local / student123</span>
        </div>
    </div>
</section>

<%@ include file="includes/footer.jspf" %>
