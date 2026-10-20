<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Products | ShopSphere</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>
<header class="site-header">
    <nav class="navbar container py-3" aria-label="Main navigation">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/">ShopSphere</a>
        <div class="d-flex align-items-center gap-2">
            <c:if test="${not empty sessionScope.userId and sessionScope.userRole eq 'CUSTOMER'}">
                <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/cart">Cart</a>
                <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/orders">My Orders</a>
            </c:if>
            <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/">Home</a>
        </div>
    </nav>
</header>

<main class="container py-5">
    <div class="catalog-heading mb-4">
        <p class="eyebrow mb-1">ShopSphere catalog</p>
        <h1 class="h2 mb-0">Products</h1>
    </div>

    <c:choose>
        <c:when test="${not empty errorMessage}">
            <div class="alert alert-danger" role="alert"><c:out value="${errorMessage}" /></div>
        </c:when>
        <c:when test="${empty products}">
            <p class="catalog-empty">There are no active products to display.</p>
        </c:when>
        <c:otherwise>
            <fmt:setLocale value="en_IN" />
            <div class="row g-3">
                <c:forEach items="${products}" var="product">
                    <div class="col-sm-6 col-xl-4">
                        <article class="product-card h-100">
                            <c:choose>
                                <c:when test="${not empty product.imageUrl}">
                                    <img class="product-image" src="${fn:escapeXml(product.imageUrl)}"
                                         alt="${fn:escapeXml(product.name)}" loading="lazy">
                                </c:when>
                                <c:otherwise>
                                    <div class="product-image-placeholder" aria-hidden="true">No image</div>
                                </c:otherwise>
                            </c:choose>
                            <div class="product-card-body">
                                <c:if test="${not empty product.categoryName}">
                                    <p class="eyebrow mb-2"><c:out value="${product.categoryName}" /></p>
                                </c:if>
                                <h2 class="h5"><c:out value="${product.name}" /></h2>
                                <p class="product-description">
                                    <c:choose>
                                        <c:when test="${not empty product.description}">
                                            <c:out value="${product.description}" />
                                        </c:when>
                                        <c:otherwise>No description available.</c:otherwise>
                                    </c:choose>
                                </p>
                                <div class="product-card-footer">
                                    <strong class="product-price"><fmt:formatNumber value="${product.price}" type="currency" currencyCode="INR" /></strong>
                                    <span class="small text-secondary">
                                        <c:choose>
                                            <c:when test="${product.stockQuantity gt 0}">In stock</c:when>
                                            <c:otherwise>Out of stock</c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                                <form class="mt-3" action="${pageContext.request.contextPath}/cart/add" method="post">
                                    <input type="hidden" name="productId" value="${product.id}">
                                    <button class="btn btn-primary w-100" type="submit"
                                            <c:if test="${product.stockQuantity le 0}">disabled</c:if>>Add to Cart</button>
                                </form>
                            </div>
                        </article>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</main>
</body>
</html>