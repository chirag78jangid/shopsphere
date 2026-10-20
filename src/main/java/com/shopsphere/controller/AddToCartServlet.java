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

@WebServlet("/cart/add")
public class AddToCartServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(AddToCartServlet.class.getName());
    private final CartDAO cartDAO = new CartDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long userId = CartServlet.getUserId(request);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Long productId = parsePositiveLong(request.getParameter("productId"));
        if (productId == null) {
            redirectToCart(request, response, "invalid-product");
            return;
        }

        try {
            CartResult result = cartDAO.addProduct(userId, productId, 1);
            switch (result) {
                case SUCCESS -> response.sendRedirect(request.getContextPath() + "/cart?added=1");
                case PRODUCT_NOT_FOUND -> redirectToCart(request, response, "not-found");
                case OUT_OF_STOCK -> redirectToCart(request, response, "out-of-stock");
                case EXCEEDS_STOCK -> redirectToCart(request, response, "stock-limit");
                default -> redirectToCart(request, response, "invalid-quantity");
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to add product to cart.", exception);
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

    private static void redirectToCart(HttpServletRequest request, HttpServletResponse response, String error)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/cart?error=" + error);
    }
}