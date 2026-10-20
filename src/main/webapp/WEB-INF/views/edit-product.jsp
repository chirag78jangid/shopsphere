<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Edit Product | ShopSphere</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>
<header class="site-header">
    <nav class="navbar container py-3" aria-label="Admin navigation">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/">ShopSphere</a>
        <div class="d-flex align-items-center gap-3 ms-auto">
            <a href="${pageContext.request.contextPath}/admin/products">Manage Products</a>
            <form action="${pageContext.request.contextPath}/logout" method="post" class="m-0">
                <button class="btn btn-outline-dark btn-sm" type="submit">Log out</button>
            </form>
        </div>
    </nav>
</header>
<main class="container py-5">
    <section class="auth-panel add-product-panel mx-auto">
        <p class="eyebrow mb-1">Catalog</p>
        <h1 class="h2 mb-4">Edit Product</h1>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger" role="alert"><c:out value="${errorMessage}" /></div>
        </c:if>

        <form action="${pageContext.request.contextPath}/admin/products/edit" method="post">
            <input type="hidden" name="id" value="${product.id}">
            <div class="mb-3">
                <label class="form-label" for="name">Product name</label>
                <input class="form-control" id="name" name="name" type="text" maxlength="180"
                       required value="<c:out value='${productName}' />">
            </div>
            <div class="mb-3">
                <label class="form-label" for="description">Description</label>
                <textarea class="form-control" id="description" name="description" rows="4"><c:out value="${description}" /></textarea>
            </div>
            <div class="row g-3 mb-3">
                <div class="col-sm-6">
                    <label class="form-label" for="price">Price</label>
                    <input class="form-control" id="price" name="price" type="number" min="0.01" step="0.01"
                           required value="<c:out value='${price}' />">
                </div>
                <div class="col-sm-6">
                    <label class="form-label" for="stockQuantity">Stock quantity</label>
                    <input class="form-control" id="stockQuantity" name="stockQuantity" type="number" min="0"
                           step="1" required value="<c:out value='${stockQuantity}' />">
                </div>
            </div>
            <div class="mb-3">
                <label class="form-label" for="categoryId">Category</label>
                <select class="form-select" id="categoryId" name="categoryId" required>
                    <option value="">Select a category</option>
                    <c:forEach items="${categories}" var="category">
                        <option value="${category.id}" <c:if test="${category.id eq selectedCategoryId}">selected</c:if>>
                            <c:out value="${category.name}" />
                        </option>
                    </c:forEach>
                </select>
            </div>
            <div class="mb-4">
                <label class="form-label" for="imageUrl">Image URL <span class="text-secondary">(optional)</span></label>
                <input class="form-control" id="imageUrl" name="imageUrl" type="url" maxlength="500"
                       value="<c:out value='${imageUrl}' />">
            </div>
            <div class="d-flex flex-wrap gap-2">
                <button class="btn btn-primary" type="submit">Save changes</button>
                <a class="btn btn-outline-dark" href="${pageContext.request.contextPath}/admin/products">Cancel</a>
            </div>
        </form>
    </section>
</main>
</body>
</html>