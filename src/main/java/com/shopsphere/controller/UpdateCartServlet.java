package com.shopsphere.controller;

import com.shopsphere.dao.CartDAO;
import com.shopsphere.dao.CartDAO.CartResult;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/cart/update")
public class UpdateCartServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(UpdateCartServlet.class.getName());
    private final CartDAO cartDAO = new CartDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long userId = CartServlet.getUserId(request);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Long productId = parsePositiveLong(request.getParameter("productId"));
        Integer quantity = parseInteger(request.getParameter("quantity"));
        if (productId == null) {
            redirectToCart(request, response, "invalid-product");
            return;
        }
        if (quantity == null || quantity < 1) {
            redirectToCart(request, response, "invalid-quantity");
            return;
        }

        try {
            CartResult result = cartDAO.updateQuantity(userId, productId, quantity);
            switch (result) {
                case SUCCESS -> response.sendRedirect(request.getContextPath() + "/cart?updated=1");
                case EXCEEDS_STOCK -> redirectToCart(request, response, "stock-limit");
                case PRODUCT_NOT_FOUND, CART_ITEM_NOT_FOUND -> redirectToCart(request, response, "not-found");
                default -> redirectToCart(request, response, "invalid-quantity");
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to update cart quantity.", exception);
            redirectToCart(request, response, "database");
        }
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

    private static Integer parseInteger(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private static void redirectToCart(HttpServletRequest request, HttpServletResponse response, String error)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/cart?error=" + error);
    }
}