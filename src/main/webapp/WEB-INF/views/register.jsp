<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Register | ShopSphere</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>
<header class="site-header">
    <nav class="navbar container py-3" aria-label="Main navigation">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/">ShopSphere</a>
        <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/login">Log in</a>
    </nav>
</header>
<main class="container py-5">
    <section class="auth-panel mx-auto">
        <p class="eyebrow mb-1">New customer</p>
        <h1 class="h2 mb-4">Create your account</h1>
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger" role="alert"><c:out value="${errorMessage}" /></div>
        </c:if>
        <form action="${pageContext.request.contextPath}/register" method="post">
            <div class="mb-3">
                <label class="form-label" for="fullName">Full name</label>
                <input class="form-control" id="fullName" name="fullName" type="text" maxlength="120"
                       autocomplete="name" required value="<c:out value='${fullName}' />">
            </div>
            <div class="mb-3">
                <label class="form-label" for="email">Email</label>
                <input class="form-control" id="email" name="email" type="email" maxlength="190"
                       autocomplete="email" required value="<c:out value='${email}' />">
            </div>
            <div class="mb-3">
                <label class="form-label" for="phone">Phone <span class="text-secondary">(optional)</span></label>
                <input class="form-control" id="phone" name="phone" type="tel" maxlength="30"
                       autocomplete="tel" value="<c:out value='${phone}' />">
            </div>
            <div class="mb-3">
                <label class="form-label" for="password">Password</label>
                <input class="form-control" id="password" name="password" type="password" minlength="8"
                       maxlength="72" autocomplete="new-password" required>
            </div>
            <div class="mb-4">
                <label class="form-label" for="confirmPassword">Confirm password</label>
                <input class="form-control" id="confirmPassword" name="confirmPassword" type="password"
                       minlength="8" maxlength="72" autocomplete="new-password" required>
            </div>
            <button class="btn btn-primary w-100" type="submit">Create account</button>
        </form>
        <p class="mt-3 mb-0 text-secondary">Already registered? <a href="${pageContext.request.contextPath}/login">Log in</a></p>
    </section>
</main>
</body>
</html>