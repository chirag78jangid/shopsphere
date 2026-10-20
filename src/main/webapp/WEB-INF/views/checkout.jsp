<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Checkout | ShopSphere</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>
<header class="site-header">
    <nav class="navbar container py-3" aria-label="Customer navigation">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/">ShopSphere</a>
        <div class="d-flex align-items-center gap-2 ms-auto">
            <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/orders">My Orders</a>
            <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/cart">Cart</a>
        </div>
    </nav>
</header>
<main class="container py-5">
    <div class="catalog-heading mb-4">
        <p class="eyebrow mb-1">ShopSphere</p>
        <h1 class="h2 mb-0">Checkout</h1>
    </div>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger" role="alert"><c:out value="${errorMessage}" /></div>
    </c:if>

    <div class="row g-4">
        <section class="col-lg-7" aria-labelledby="shipping-heading">
            <div class="checkout-panel">
                <h2 class="h5 mb-3" id="shipping-heading">Shipping address</h2>
                <form action="${pageContext.request.contextPath}/checkout" method="post" id="checkout-form">
                    <div class="mb-3">
                        <label class="form-label" for="addressId">Choose an address</label>
                        <select class="form-select" id="addressId" name="addressId" required>
                            <c:forEach items="${addresses}" var="address">
                                <option value="${address.id}"
                                        <c:if test="${selectedAddressId eq address.id}">selected</c:if>>
                                    <c:out value="${address.addressLine}" />, <c:out value="${address.city}" />,
                                    <c:out value="${address.state}" /> <c:out value="${address.postalCode}" />
                                    <c:if test="${address.defaultAddress}">(Default)</c:if>
                                </option>
                            </c:forEach>
                            <option value="new" <c:if test="${selectedAddressIsNew}">selected</c:if>>Enter a new address</option>
                        </select>
                    </div>

                    <fieldset id="new-address-fields" class="checkout-address-fields">
                        <legend class="h6">New shipping address</legend>
                        <div class="mb-3">
                            <label class="form-label" for="addressLine">Address</label>
                            <input class="form-control" id="addressLine" name="addressLine" maxlength="255"
                                   value="<c:out value='${addressLine}' />">
                        </div>
                        <div class="row g-3">
                            <div class="col-sm-6 mb-3">
                                <label class="form-label" for="city">City</label>
                                <input class="form-control" id="city" name="city" maxlength="100"
                                       value="<c:out value='${city}' />">
                            </div>
                            <div class="col-sm-6 mb-3">
                                <label class="form-label" for="state">State</label>
                                <input class="form-control" id="state" name="state" maxlength="100"
                                       value="<c:out value='${state}' />">
                            </div>
                            <div class="col-sm-6 mb-3">
                                <label class="form-label" for="postalCode">Postal code</label>
                                <input class="form-control" id="postalCode" name="postalCode" maxlength="20"
                                       value="<c:out value='${postalCode}' />">
                            </div>
                            <div class="col-sm-6 mb-3">
                                <label class="form-label" for="country">Country</label>
                                <input class="form-control" id="country" name="country" maxlength="100"
                                       value="<c:out value='${country}' />">
                            </div>
                        </div>
                    </fieldset>

                    <button class="btn btn-primary mt-3" type="submit">Place Order</button>
                </form>
            </div>
        </section>

        <section class="col-lg-5" aria-labelledby="review-heading">
            <div class="checkout-panel">
                <h2 class="h5 mb-3" id="review-heading">Order summary</h2>
                <div class="checkout-lines">
                    <c:forEach items="${cartItems}" var="item">
                        <div class="checkout-line">
                            <div>
                                <strong><c:out value="${item.product.name}" /></strong>
                                <span class="d-block small text-secondary">Qty <c:out value="${item.quantity}" /> x
                                    <fmt:formatNumber value="${item.product.price}" type="currency" currencyCode="INR" />
                                </span>
                            </div>
                            <strong><fmt:formatNumber value="${item.subtotal}" type="currency" currencyCode="INR" /></strong>
                        </div>
                    </c:forEach>
                </div>
                <div class="checkout-total">
                    <span>Total</span>
                    <strong><fmt:formatNumber value="${cartTotal}" type="currency" currencyCode="INR" /></strong>
                </div>
                <p class="small text-secondary mb-0 mt-3">Payment method: Cash on delivery. Payment status: Pending.</p>
            </div>
        </section>
    </div>
</main>
<script>
    const addressSelect = document.getElementById('addressId');
    const addressFields = document.getElementById('new-address-fields');
    const addressInputs = addressFields.querySelectorAll('input');

    function toggleNewAddressFields() {
        const useNewAddress = addressSelect.value === 'new';
        addressFields.hidden = !useNewAddress;
        addressInputs.forEach((input) => {
            input.disabled = !useNewAddress;
            input.required = useNewAddress;
        });
    }

    addressSelect.addEventListener('change', toggleNewAddressFields);
    toggleNewAddressFields();
</script>
</body>
</html>