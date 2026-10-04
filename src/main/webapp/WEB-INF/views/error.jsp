<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%
    request.setAttribute("pageTitle", "Something went wrong");
    // Fill in friendly defaults when Tomcat forwards here for 404 / 500
    if (request.getAttribute("errorTitle") == null) {
        Object code = request.getAttribute("jakarta.servlet.error.status_code");
        if (code != null && Integer.valueOf(404).equals(code)) {
            request.setAttribute("errorTitle", "Page not found");
            request.setAttribute("errorMessage", "The page you're looking for doesn't exist or has moved.");
        } else {
            request.setAttribute("errorTitle", "Something went wrong");
            request.setAttribute("errorMessage", "An unexpected error occurred. Please try again.");
        }
    }
%>
<%@ include file="includes/header.jspf" %>

<section class="container narrow fade-in">
    <div class="empty error-box">
        <p class="eyebrow accent">Error</p>
        <h1><c:out value="${errorTitle}" /></h1>
        <p><c:out value="${errorMessage}" /></p>
        <div class="actions center">
            <a href="${ctx}/home" class="btn btn-dark">Go home</a>
            <a href="javascript:history.back()" class="btn btn-outline">Go back</a>
        </div>
    </div>
</section>

<%@ include file="includes/footer.jspf" %>
