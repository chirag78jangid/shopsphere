<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>My Orders | ShopSphere</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>
<header class="site-header">
    <nav class="navbar container py-3" aria-label="Customer navigation">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/">ShopSphere</a>
        <div class="d-flex align-items-center gap-2 ms-auto">
            <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/products">Shop</a>
            <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/cart">Cart</a>
        </div>
    </nav>
</header>
<main class="container py-5">
    <div class="catalog-heading mb-4">
        <p class="eyebrow mb-1">Your account</p>
        <h1 class="h2 mb-0">My Orders</h1>
    </div>

    <c:choose>
        <c:when test="${empty orders}">
            <p class="catalog-empty">You have not placed any orders yet. <a href="${pageContext.request.contextPath}/products">Browse products</a>.</p>
        </c:when>
        <c:otherwise>
            <fmt:setLocale value="en_IN" />
            <div class="order-table-wrap">
                <table class="table align-middle">
                    <thead class="table-light">
                    <tr>
                        <th scope="col">Order</th>
                        <th scope="col">Date</th>
                        <th scope="col">Status</th>
                        <th scope="col">Total</th>
                        <th scope="col">Details</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${orders}" var="order">
                        <tr>
                            <th scope="row">#<c:out value="${order.id}" /></th>
                            <td><c:out value="${order.createdAt}" /></td>
                            <td><c:out value="${order.status}" /></td>
                            <td><fmt:formatNumber value="${order.totalAmount}" type="currency" currencyCode="INR" /></td>
                            <td><a class="btn btn-outline-primary btn-sm"
                                   href="${pageContext.request.contextPath}/orders/view?id=${order.id}">View order</a></td>
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