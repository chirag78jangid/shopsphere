<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Order Details | ShopSphere</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>
<header class="site-header">
    <nav class="navbar container py-3" aria-label="Customer navigation">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/">ShopSphere</a>
        <div class="d-flex align-items-center gap-2 ms-auto">
            <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/orders">My Orders</a>
            <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/products">Shop</a>
        </div>
    </nav>
</header>
<main class="container py-5">
    <c:if test="${orderPlaced}">
        <div class="alert alert-success" role="status">Your order has been placed successfully.</div>
    </c:if>
    <div class="catalog-heading mb-4">
        <p class="eyebrow mb-1">ShopSphere</p>
        <h1 class="h2 mb-2">Order #<c:out value="${order.id}" /></h1>
        <p class="text-secondary mb-0">Status: <c:out value="${order.status}" /></p>
    </div>

    <div class="row g-4">
        <section class="col-lg-7" aria-labelledby="items-heading">
            <div class="order-panel">
                <h2 class="h5 mb-3" id="items-heading">Items</h2>
                <fmt:setLocale value="en_IN" />
                <div class="order-lines">
                    <c:forEach items="${orderItems}" var="item">
                        <div class="checkout-line">
                            <div>
                                <strong><c:out value="${item.productName}" /></strong>
                                <span class="d-block small text-secondary">Qty <c:out value="${item.quantity}" /> x
                                    <fmt:formatNumber value="${item.unitPrice}" type="currency" currencyCode="INR" />
                                </span>
                            </div>
                            <strong><fmt:formatNumber value="${item.subtotal}" type="currency" currencyCode="INR" /></strong>
                        </div>
                    </c:forEach>
                </div>
                <div class="checkout-total">
                    <span>Order total</span>
                    <strong><fmt:formatNumber value="${order.totalAmount}" type="currency" currencyCode="INR" /></strong>
                </div>
            </div>
        </section>
        <section class="col-lg-5" aria-labelledby="address-heading">
            <div class="order-panel">
                <h2 class="h5 mb-3" id="address-heading">Shipping address</h2>
                <p class="mb-0"><c:out value="${order.shippingAddress}" /></p>
                <p class="small text-secondary mt-3 mb-0">Payment: Cash on delivery, pending.</p>
            </div>
        </section>
    </div>
</main>
</body>
</html>