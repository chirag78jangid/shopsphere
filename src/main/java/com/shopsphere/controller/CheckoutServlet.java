package com.shopsphere.controller;

import com.shopsphere.dao.CartDAO;
import com.shopsphere.dao.OrderDAO;
import com.shopsphere.dao.OrderDAO.PlacementResult;
import com.shopsphere.dao.OrderDAO.PlacementStatus;
import com.shopsphere.model.Address;
import com.shopsphere.model.CartItem;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(CheckoutServlet.class.getName());
    private final CartDAO cartDAO = new CartDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long userId = CartServlet.getUserId(request);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String error = request.getParameter("error");
        if ("address".equals(error)) {
            request.setAttribute("errorMessage", "Choose a saved address or enter a valid new address.");
        } else if ("stock".equals(error)) {
            request.setAttribute("errorMessage", "One or more products no longer have enough stock. Review your cart and try again.");
        } else if ("retry".equals(error)) {
            request.setAttribute("errorMessage", "Your cart changed during checkout. Please review it and try again.");
        } else if ("failed".equals(error)) {
            request.setAttribute("errorMessage", "We could not place your order. No order was submitted; please try again.");
        }

        showCheckout(request, response, userId);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Long userId = CartServlet.getUserId(request);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String selection = value(request, "addressId");
        Long addressId = null;
        Address newAddress = null;
        if ("new".equals(selection)) {
            newAddress = new Address(0, value(request, "addressLine").trim(),
                    value(request, "city").trim(), value(request, "state").trim(),
                    value(request, "postalCode").trim(), value(request, "country").trim(), false);
            if (!isValidAddress(newAddress)) {
                preserveAddress(request, selection, newAddress);
                request.setAttribute("errorMessage", "Complete each address field within its length limit.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                showCheckout(request, response, userId);
                return;
            }
        } else {
            addressId = parsePositiveLong(selection);
            if (addressId == null) {
                request.setAttribute("errorMessage", "Choose a saved address or enter a new one.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                showCheckout(request, response, userId);
                return;
            }
        }

        try {
            PlacementResult result = orderDAO.placeOrder(userId, addressId, newAddress);
            if (result.status() == PlacementStatus.SUCCESS) {
                response.sendRedirect(request.getContextPath() + "/orders/view?id=" +
                        result.orderId() + "&placed=1");
                return;
            }
            switch (result.status()) {
                case USER_NOT_FOUND -> response.sendRedirect(request.getContextPath() + "/login");
                case EMPTY_CART -> response.sendRedirect(request.getContextPath() + "/cart?error=empty-checkout");
                case INVALID_ADDRESS -> response.sendRedirect(request.getContextPath() + "/checkout?error=address");
                case INSUFFICIENT_STOCK -> response.sendRedirect(request.getContextPath() + "/checkout?error=stock");
                case TOTAL_TOO_LARGE, CART_CHANGED -> response.sendRedirect(request.getContextPath() + "/checkout?error=retry");
                default -> response.sendRedirect(request.getContextPath() + "/checkout?error=failed");
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to place customer order.", exception);
            response.sendRedirect(request.getContextPath() + "/checkout?error=failed");
        }
    }

    private void showCheckout(HttpServletRequest request, HttpServletResponse response, long userId)
            throws ServletException, IOException {
        try {
            List<CartItem> items = cartDAO.findCartItems(userId);
            if (items.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/cart?error=empty-checkout");
                return;
            }
            List<Address> addresses = orderDAO.findAddressesForUser(userId);
            request.setAttribute("cartItems", items);
            request.setAttribute("cartTotal", items.stream()
                    .map(CartItem::getSubtotal)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
            request.setAttribute("addresses", addresses);
            if (request.getAttribute("selectedAddressId") == null) {
                Address selectedAddress = addresses.stream()
                        .filter(Address::isDefaultAddress)
                    .findFirst()
                    .orElseGet(() -> addresses.stream().findFirst().orElse(null));
                request.setAttribute("selectedAddressId", selectedAddress == null ? null : selectedAddress.getId());
                request.setAttribute("selectedAddressIsNew", selectedAddress == null);
            }
            request.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(request, response);
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to load checkout details.", exception);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private static boolean isValidAddress(Address address) {
        String formattedAddress = address.getFormattedAddress();
        return !address.getAddressLine().isBlank() && address.getAddressLine().length() <= 255
                && !address.getCity().isBlank() && address.getCity().length() <= 100
                && !address.getState().isBlank() && address.getState().length() <= 100
                && !address.getPostalCode().isBlank() && address.getPostalCode().length() <= 20
                && !address.getCountry().isBlank() && address.getCountry().length() <= 100
                && formattedAddress.length() <= 500;
    }

    private static void preserveAddress(HttpServletRequest request, String selection, Address address) {
        request.setAttribute("selectedAddressId", 0L);
        request.setAttribute("selectedAddressIsNew", "new".equals(selection));
        request.setAttribute("addressLine", address.getAddressLine());
        request.setAttribute("city", address.getCity());
        request.setAttribute("state", address.getState());
        request.setAttribute("postalCode", address.getPostalCode());
        request.setAttribute("country", address.getCountry());
    }

    private static Long parsePositiveLong(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            long parsed = Long.parseLong(value);
            return parsed > 0 ? parsed : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private static String value(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null ? "" : value;
    }
}