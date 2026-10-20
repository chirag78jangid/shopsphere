<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Your Cart | ShopSphere</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>
<header class="site-header">
    <nav class="navbar container py-3" aria-label="Customer navigation">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/">ShopSphere</a>
        <div class="d-flex align-items-center gap-2 ms-auto">
            <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/products">Continue shopping</a>
            <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/orders">My Orders</a>
            <form action="${pageContext.request.contextPath}/logout" method="post" class="m-0">
                <button class="btn btn-outline-dark btn-sm" type="submit">Log out</button>
            </form>
        </div>
    </nav>
</header>
<main class="container py-5">
    <div class="catalog-heading mb-4">
        <p class="eyebrow mb-1">ShopSphere</p>
        <h1 class="h2 mb-0">Your Cart</h1>
    </div>

    <c:if test="${not empty successMessage}">
        <div class="alert alert-success" role="status"><c:out value="${successMessage}" /></div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger" role="alert"><c:out value="${errorMessage}" /></div>
    </c:if>

    <c:choose>
        <c:when test="${empty cartItems}">
            <p class="catalog-empty">Your cart is empty. <a href="${pageContext.request.contextPath}/products">Browse products</a>.</p>
        </c:when>
        <c:otherwise>
            <fmt:setLocale value="en_IN" />
            <div class="cart-table-wrap">
                <table class="table align-middle">
                    <thead class="table-light">
                    <tr>
                        <th scope="col">Product</th>
                        <th scope="col">Price</th>
                        <th scope="col">Quantity</th>
                        <th scope="col">Subtotal</th>
                        <th scope="col">Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${cartItems}" var="item">
                        <tr>
                            <th scope="row"><c:out value="${item.product.name}" /></th>
                            <td><fmt:formatNumber value="${item.product.price}" type="currency" currencyCode="INR" /></td>
                            <td>
                                <c:if test="${item.quantity gt item.product.stockQuantity}">
                                    <span class="d-block small text-danger mb-1">Stock is now ${item.product.stockQuantity}.</span>
                                </c:if>
                                <form class="cart-quantity-form" action="${pageContext.request.contextPath}/cart/update" method="post">
                                    <input type="hidden" name="productId" value="${item.product.id}">
                                    <label class="visually-hidden" for="quantity-${item.product.id}">Quantity for <c:out value="${item.product.name}" /></label>
                                    <input class="form-control form-control-sm" id="quantity-${item.product.id}"
                                           name="quantity" type="number" min="1" max="${item.product.stockQuantity}"
                                           value="${item.quantity}" required>
                                    <button class="btn btn-outline-primary btn-sm" type="submit"
                                            <c:if test="${item.product.stockQuantity lt 1}">disabled</c:if>>Update</button>
                                </form>
                            </td>
                            <td><fmt:formatNumber value="${item.subtotal}" type="currency" currencyCode="INR" /></td>
                            <td>
                                <form action="${pageContext.request.contextPath}/cart/remove" method="post" class="m-0">
                                    <input type="hidden" name="productId" value="${item.product.id}">
                                    <button class="btn btn-outline-danger btn-sm" type="submit">Remove</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                    <tfoot>
                    <tr>
                        <th class="text-end" colspan="3" scope="row">Cart total</th>
                        <td colspan="2"><strong><fmt:formatNumber value="${cartTotal}" type="currency" currencyCode="INR" /></strong></td>
                    </tr>
                    </tfoot>
                </table>
            </div>
            <div class="d-flex flex-wrap align-items-center justify-content-between gap-3 mt-3">
                <p class="small text-secondary mb-0">Payment is collected on delivery.</p>
                <a class="btn btn-primary" href="${pageContext.request.contextPath}/checkout">Proceed to Checkout</a>
            </div>
        </c:otherwise>
    </c:choose>
</main>
</body>
</html>