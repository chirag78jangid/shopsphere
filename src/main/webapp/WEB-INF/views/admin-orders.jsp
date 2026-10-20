<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Manage Orders | ShopSphere</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>
<header class="site-header">
    <nav class="navbar container py-3" aria-label="Admin navigation">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/">ShopSphere</a>
        <div class="d-flex align-items-center gap-3 ms-auto">
            <a href="${pageContext.request.contextPath}/admin/dashboard">Admin Dashboard</a>
            <form action="${pageContext.request.contextPath}/logout" method="post" class="m-0">
                <button class="btn btn-outline-dark btn-sm" type="submit">Log out</button>
            </form>
        </div>
    </nav>
</header>
<main class="container py-5">
    <div class="catalog-heading mb-4">
        <p class="eyebrow mb-1">ShopSphere administration</p>
        <h1 class="h2 mb-0">Manage Orders</h1>
    </div>

    <c:if test="${not empty successMessage}">
        <div class="alert alert-success" role="status"><c:out value="${successMessage}" /></div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger" role="alert"><c:out value="${errorMessage}" /></div>
    </c:if>

    <c:choose>
        <c:when test="${empty orders}">
            <p class="catalog-empty">There are no customer orders yet.</p>
        </c:when>
        <c:otherwise>
            <fmt:setLocale value="en_IN" />
            <div class="admin-table-wrap">
                <table class="table table-hover align-middle">
                    <thead class="table-light">
                    <tr>
                        <th scope="col">Order ID</th>
                        <th scope="col">Customer</th>
                        <th scope="col">Order date</th>
                        <th scope="col">Total</th>
                        <th scope="col">Order status</th>
                        <th scope="col">Payment status</th>
                        <th scope="col">Details</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${orders}" var="order">
                        <tr>
                            <th scope="row">#<c:out value="${order.id}" /></th>
                            <td><c:out value="${order.customerEmail}" /></td>
                            <td><c:out value="${order.createdAt}" /></td>
                            <td><fmt:formatNumber value="${order.totalAmount}" type="currency" currencyCode="INR" /></td>
                            <td><c:out value="${order.status}" /></td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty order.paymentStatus}"><c:out value="${order.paymentStatus}" /></c:when>
                                    <c:otherwise>No payment record</c:otherwise>
                                </c:choose>
                            </td>
                            <td><a class="btn btn-outline-primary btn-sm"
                                   href="${pageContext.request.contextPath}/admin/orders/view?id=${order.id}">View</a></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</main>
</body>
</html>