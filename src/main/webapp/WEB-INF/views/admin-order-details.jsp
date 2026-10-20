<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Order Details | ShopSphere Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>
<header class="site-header">
    <nav class="navbar container py-3" aria-label="Admin navigation">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/">ShopSphere</a>
        <div class="d-flex align-items-center gap-3 ms-auto">
            <a href="${pageContext.request.contextPath}/admin/orders">Manage Orders</a>
            <form action="${pageContext.request.contextPath}/logout" method="post" class="m-0">
                <button class="btn btn-outline-dark btn-sm" type="submit">Log out</button>
            </form>
        </div>
    </nav>
</header>
<main class="container py-5">
    <c:if test="${statusUpdated}">
        <div class="alert alert-success" role="status">Order status updated successfully.</div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger" role="alert"><c:out value="${errorMessage}" /></div>
    </c:if>

    <div class="catalog-heading mb-4">
        <p class="eyebrow mb-1">Order administration</p>
        <h1 class="h2 mb-2">Order #<c:out value="${order.id}" /></h1>
        <p class="text-secondary mb-0">Customer: <c:out value="${order.customerEmail}" /></p>
    </div>

    <div class="row g-4">
        <section class="col-lg-7" aria-labelledby="items-heading">
            <div class="order-panel">
                <h2 class="h5 mb-3" id="items-heading">Order items</h2>
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
                    <span>Total</span>
                    <strong><fmt:formatNumber value="${order.totalAmount}" type="currency" currencyCode="INR" /></strong>
                </div>
            </div>
        </section>
        <section class="col-lg-5" aria-labelledby="summary-heading">
            <div class="order-panel">
                <h2 class="h5 mb-3" id="summary-heading">Order summary</h2>
                <dl class="row mb-4">
                    <dt class="col-5">Date</dt>
                    <dd class="col-7"><c:out value="${order.createdAt}" /></dd>
                    <dt class="col-5">Payment</dt>
                    <dd class="col-7">
                        <c:choose>
                            <c:when test="${not empty order.paymentStatus}"><c:out value="${order.paymentStatus}" /></c:when>
                            <c:otherwise>No payment record</c:otherwise>
                        </c:choose>
                    </dd>
                    <dt class="col-5">Shipping</dt>
                    <dd class="col-7"><c:out value="${order.shippingAddress}" /></dd>
                </dl>

                <h3 class="h6">Update order status</h3>
                <form action="${pageContext.request.contextPath}/admin/orders/view" method="post">
                    <input type="hidden" name="id" value="${order.id}">
                    <label class="visually-hidden" for="status">Order status</label>
                    <div class="d-flex gap-2">
                        <select class="form-select" id="status" name="status" required>
                            <c:forEach items="${orderStatuses}" var="status">
                                <option value="${status}" <c:if test="${status eq order.status}">selected</c:if>>
                                    <c:out value="${status}" />
                                </option>
                            </c:forEach>
                        </select>
                        <button class="btn btn-primary flex-shrink-0" type="submit">Save</button>
                    </div>
                </form>
            </div>
        </section>
    </div>
</main>
</body>
</html>