<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>ShopSphere | E-Commerce Management System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>
<header class="site-header">
    <nav class="navbar navbar-expand-lg container py-3" aria-label="Main navigation">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/">ShopSphere</a>
        <div class="d-flex align-items-center gap-3 ms-auto">
            <c:choose>
                <c:when test="${not empty sessionScope.userId}">
                    <span class="navbar-text">Signed in as <c:out value="${sessionScope.userEmail}" /></span>
                    <c:if test="${sessionScope.userRole eq 'CUSTOMER'}">
                        <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/cart">Cart</a>
                        <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/orders">My Orders</a>
                    </c:if>
                    <c:if test="${sessionScope.userRole eq 'ADMIN'}">
                        <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/admin/dashboard">Admin</a>
                    </c:if>
                    <form action="${pageContext.request.contextPath}/logout" method="post" class="m-0">
                        <button class="btn btn-outline-dark btn-sm" type="submit">Log out</button>
                    </form>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/login">Log in</a>
                    <a class="btn btn-primary btn-sm" href="${pageContext.request.contextPath}/register">Register</a>
                </c:otherwise>
            </c:choose>
        </div>
    </nav>
</header>

<main class="container py-5">
    <c:if test="${param.accessDenied eq '1'}">
        <div class="alert alert-danger" role="alert">You do not have permission to access that page.</div>
    </c:if>
    <section class="welcome-panel p-4 p-md-5">
        <p class="eyebrow mb-2">E-Commerce Management System</p>
        <h1 class="display-5 fw-bold">Welcome to ShopSphere</h1>
        <p class="lead col-lg-8 mb-4">
            A Java 17, Jakarta Servlet and JSP project built with a clear MVC structure
            and a MySQL database.
        </p>
        <div class="d-flex flex-wrap gap-2">
            <a class="btn btn-primary btn-lg" href="#project-status">Project status</a>
            <a class="btn btn-outline-dark btn-lg" href="${pageContext.request.contextPath}/products">Browse products</a>
        </div>
    </section>

    <section id="sample-catalog" class="py-5">
        <p class="eyebrow mb-1">Live catalog</p>
        <h2 class="h3">Explore products</h2>
        <p class="text-secondary">Browse active products loaded from the ShopSphere database.</p>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/products">View all products</a>
    </section>

    <section id="project-status" class="status-panel p-4">
        <h2 class="h5">ShopSphere catalog</h2>
        <p class="mb-0">Product listings are retrieved from MySQL and include current prices and stock availability.</p>
    </section>
</main>

<footer class="container py-4 text-secondary small">
    <span>&copy; <span id="current-year">2026</span> ShopSphere</span>
</footer>
<script src="${pageContext.request.contextPath}/js/script.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>