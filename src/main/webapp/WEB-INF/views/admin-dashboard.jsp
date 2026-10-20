<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Admin Dashboard | ShopSphere</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>
<header class="site-header">
    <nav class="navbar container py-3" aria-label="Admin navigation">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/">ShopSphere</a>
        <div class="d-flex align-items-center gap-3 ms-auto">
            <span class="navbar-text"><c:out value="${sessionScope.userEmail}" /></span>
            <form action="${pageContext.request.contextPath}/logout" method="post" class="m-0">
                <button class="btn btn-outline-dark btn-sm" type="submit">Log out</button>
            </form>
        </div>
    </nav>
</header>
<main class="container py-5">
    <div class="catalog-heading mb-4">
        <p class="eyebrow mb-1">ShopSphere administration</p>
        <h1 class="h2 mb-2">Admin Dashboard</h1>
        <p class="text-secondary mb-0">Welcome, <c:out value="${sessionScope.userEmail}" />.</p>
    </div>

    <c:if test="${not empty noticeMessage}">
        <div class="alert alert-info" role="status"><c:out value="${noticeMessage}" /></div>
    </c:if>
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success" role="status"><c:out value="${successMessage}" /></div>
    </c:if>

    <div class="row g-3">
        <div class="col-md-4">
            <a class="admin-action h-100" href="${pageContext.request.contextPath}/admin/products">
                <span class="eyebrow">Catalog</span>
                <h2 class="h5 mt-2">Manage Products</h2>
                <span class="text-secondary">Open product management</span>
            </a>
        </div>
        <div class="col-md-4">
            <a class="admin-action h-100" href="${pageContext.request.contextPath}/admin/products/new">
                <span class="eyebrow">Catalog</span>
                <h2 class="h5 mt-2">Add Product</h2>
                <span class="text-secondary">Create a catalog item</span>
            </a>
        </div>
        <div class="col-md-4">
            <a class="admin-action h-100" href="${pageContext.request.contextPath}/admin/orders">
                <span class="eyebrow">Sales</span>
                <h2 class="h5 mt-2">Manage Orders</h2>
                <span class="text-secondary">Review customer orders</span>
            </a>
        </div>
    </div>
</main>
</body>
</html>