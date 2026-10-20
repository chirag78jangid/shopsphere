package com.shopsphere.controller;

import com.shopsphere.dao.CartDAO;
import com.shopsphere.model.CartItem;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(CartServlet.class.getName());
    private final CartDAO cartDAO = new CartDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long userId = getUserId(request);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        setMessage(request, "added", "Item added to your cart.");
        setMessage(request, "updated", "Cart quantity updated.");
        setMessage(request, "removed", "Item removed from your cart.");
        setError(request, "invalid-product", "That product ID is invalid.");
        setError(request, "invalid-quantity", "Enter a quantity of at least 1.");
        setError(request, "not-found", "That product or cart item could not be found.");
        setError(request, "out-of-stock", "That product is currently out of stock.");
        setError(request, "stock-limit", "The requested quantity is greater than the available stock.");
        setError(request, "database", "Your cart could not be updated. Please try again.");
        setError(request, "empty-checkout", "Your cart is empty. Add an item before checking out.");

        try {
            List<CartItem> items = cartDAO.findCartItems(userId);
            BigDecimal total = items.stream()
                    .map(CartItem::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            request.setAttribute("cartItems", items);
            request.setAttribute("cartTotal", total);
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to load the customer cart.", exception);
            request.setAttribute("errorMessage", "Your cart is temporarily unavailable. Please try again.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        request.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(request, response);
    }

    static Long getUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object userId = session == null ? null : session.getAttribute("userId");
        if (userId instanceof Number number && number.longValue() > 0) {
            return number.longValue();
        }
        return null;
    }

    private static void setMessage(HttpServletRequest request, String parameter, String message) {
        if ("1".equals(request.getParameter(parameter))) {
            request.setAttribute("successMessage", message);
        }
    }

    private static void setError(HttpServletRequest request, String expectedValue, String message) {
        if (expectedValue.equals(request.getParameter("error"))) {
            request.setAttribute("errorMessage", message);
        }
    }
}