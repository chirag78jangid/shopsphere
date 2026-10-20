<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Log in | ShopSphere</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>
<header class="site-header">
    <nav class="navbar container py-3" aria-label="Main navigation">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/">ShopSphere</a>
        <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/register">Register</a>
    </nav>
</header>
<main class="container py-5">
    <section class="auth-panel mx-auto">
        <p class="eyebrow mb-1">Welcome back</p>
        <h1 class="h2 mb-4">Log in</h1>
        <c:if test="${not empty successMessage}">
            <div class="alert alert-success" role="status"><c:out value="${successMessage}" /></div>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger" role="alert"><c:out value="${errorMessage}" /></div>
        </c:if>
        <form action="${pageContext.request.contextPath}/login" method="post">
            <div class="mb-3">
                <label class="form-label" for="email">Email</label>
                <input class="form-control" id="email" name="email" type="email" maxlength="190"
                       autocomplete="email" required value="<c:out value='${email}' />">
            </div>
            <div class="mb-4">
                <label class="form-label" for="password">Password</label>
                <input class="form-control" id="password" name="password" type="password"
                       autocomplete="current-password" required>
            </div>
            <button class="btn btn-primary w-100" type="submit">Log in</button>
        </form>
        <p class="mt-3 mb-0 text-secondary">New to ShopSphere? <a href="${pageContext.request.contextPath}/register">Create an account</a></p>
    </section>
</main>
</body>
</html>