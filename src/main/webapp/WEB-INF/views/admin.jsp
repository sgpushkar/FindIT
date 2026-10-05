<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setAttribute("pageTitle", "Admin"); request.setAttribute("nav", "admin"); %>
<%@ include file="includes/header.jspf" %>

<section class="container section fade-in">
    <p class="eyebrow">Admin</p>
    <h1 class="page-title">Overview</h1>

    <dl class="stat-cards">
        <div><dt>Total users</dt><dd>${totalUsers}</dd></div>
        <div><dt>Lost reports</dt><dd>${totalLost}</dd></div>
        <div><dt>Found reports</dt><dd>${totalFound}</dd></div>
        <div><dt>Active</dt><dd>${totalActive}</dd></div>
        <div><dt>Resolved</dt><dd>${totalResolved}</dd></div>
    </dl>

    <h2 class="sub-head">All reports</h2>
    <c:choose>
        <c:when test="${empty items}">
            <div class="empty"><h3>No reports yet.</h3></div>
        </c:when>
        <c:otherwise>
            <div class="table-wrap">
                <table class="table">
                    <thead>
                    <tr><th>Type</th><th>Item</th><th>Location</th><th>Reported by</th><th>Date</th><th>Status</th><th></th></tr>
                    </thead>
                    <tbody>
                    <c:forEach var="item" items="${items}">
                        <tr>
                            <td><span class="tag ${item.lost ? 'tag-lost' : 'tag-found'}"><c:out value="${item.type}" /></span></td>
                            <td><a class="link" href="${ctx}/item?id=${item.id}"><c:out value="${item.itemName}" /></a></td>
                            <td><c:out value="${item.location}" /></td>
                            <td><c:out value="${item.reporterName}" /></td>
                            <td class="nowrap"><c:out value="${item.displayDate}" /></td>
                            <td><span class="status ${item.resolved ? 'status-resolved' : 'status-active'}"><c:out value="${item.status}" /></span></td>
                            <td class="row-actions">
                                <c:if test="${!item.resolved}">
                                    <form action="${ctx}/resolve" method="post">
                                        <input type="hidden" name="id" value="${item.id}">
                                        <input type="hidden" name="back" value="admin">
                                        <button type="submit" class="btn btn-ghost btn-sm">Resolve</button>
                                    </form>
                                </c:if>
                                <form action="${ctx}/admin/delete" method="post"
                                      onsubmit="return confirm('Delete this report permanently?');">
                                    <input type="hidden" name="id" value="${item.id}">
                                    <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>

    <h2 class="sub-head">Registered users</h2>
    <div class="table-wrap">
        <table class="table">
            <thead><tr><th>Name</th><th>Email</th><th>Role</th><th>Joined</th></tr></thead>
            <tbody>
            <c:forEach var="u" items="${users}">
                <tr>
                    <td><c:out value="${u.name}" /></td>
                    <td><c:out value="${u.email}" /></td>
                    <td><span class="status ${u.admin ? 'status-active' : 'status-resolved'}"><c:out value="${u.role}" /></span></td>
                    <td class="nowrap"><c:out value="${fn:substring(u.createdAt, 0, 10)}" /></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</section>

<%@ include file="includes/footer.jspf" %>
