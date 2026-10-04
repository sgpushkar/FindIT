<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setAttribute("pageTitle", "Create account"); %>
<%@ include file="includes/header.jspf" %>

<section class="container narrow fade-in">
    <div class="form-card">
        <p class="eyebrow">Join FindIT</p>
        <h1>Create an account</h1>
        <p class="muted">It takes less than a minute.</p>

        <c:if test="${not empty error}">
            <div class="alert"><c:out value="${error}" /></div>
        </c:if>

        <form action="${ctx}/register" method="post" class="form" id="registerForm" novalidate>
            <label>Full name
                <input type="text" name="name" value="${fn:escapeXml(name)}" required maxlength="100" autocomplete="name">
            </label>
            <label>Email
                <input type="email" name="email" value="${fn:escapeXml(email)}" required maxlength="255" autocomplete="email">
            </label>
            <label>Password
                <input type="password" name="password" id="password" required minlength="6" autocomplete="new-password">
                <small class="hint">At least 6 characters.</small>
            </label>
            <label>Confirm password
                <input type="password" name="confirmPassword" id="confirmPassword" required autocomplete="new-password">
                <small class="hint error-text" id="matchHint" hidden>Passwords do not match.</small>
            </label>
            <button type="submit" class="btn btn-dark btn-block">Create account</button>
        </form>

        <p class="form-foot">Already have an account? <a href="${ctx}/login" class="link">Log in</a></p>
    </div>
</section>

<script src="${ctx}/js/app.js"></script>
<%@ include file="includes/footer.jspf" %>
