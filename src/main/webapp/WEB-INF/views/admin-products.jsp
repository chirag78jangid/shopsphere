<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Manage Products | ShopSphere</title>
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
    <div class="catalog-heading mb-4 d-flex flex-wrap align-items-end justify-content-between gap-3">
        <div>
            <p class="eyebrow mb-1">ShopSphere administration</p>
            <h1 class="h2 mb-0">Manage Products</h1>
        </div>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/products/new">Add Product</a>
    </div>

    <c:if test="${not empty successMessage}">
        <div class="alert alert-success" role="status"><c:out value="${successMessage}" /></div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger" role="alert"><c:out value="${errorMessage}" /></div>
    </c:if>

    <c:choose>
        <c:when test="${empty products}">
            <p class="catalog-empty">There are no products to manage yet.</p>
        </c:when>
        <c:otherwise>
            <fmt:setLocale value="en_IN" />
            <div class="admin-table-wrap">
                <table class="table table-hover align-middle">
                    <thead class="table-light">
                    <tr>
                        <th scope="col">Product</th>
                        <th scope="col">Category</th>
                        <th scope="col">Price</th>
                        <th scope="col">Stock</th>
                        <th scope="col">Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${products}" var="product">
                        <tr>
                            <th scope="row"><c:out value="${product.name}" /></th>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty product.categoryName}"><c:out value="${product.categoryName}" /></c:when>
                                    <c:otherwise>Uncategorized</c:otherwise>
                                </c:choose>
                            </td>
                            <td><fmt:formatNumber value="${product.price}" type="currency" currencyCode="INR" /></td>
                            <td><c:out value="${product.stockQuantity}" /></td>
                            <td>
                                <div class="admin-product-actions">
                                    <a class="btn btn-outline-primary btn-sm"
                                       href="${pageContext.request.contextPath}/admin/products/edit?id=${product.id}">Edit</a>
                                    <form action="${pageContext.request.contextPath}/admin/products/delete" method="post" class="m-0"
                                          onsubmit="return confirm('Are you sure you want to delete this product?');">
                                        <input type="hidden" name="id" value="${product.id}">
                                        <button class="btn btn-outline-danger btn-sm" type="submit">Delete</button>
                                    </form>
                                </div>
                            </td>
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