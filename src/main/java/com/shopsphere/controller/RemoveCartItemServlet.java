package com.shopsphere.controller;

import com.shopsphere.dao.CartDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/cart/remove")
public class RemoveCartItemServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(RemoveCartItemServlet.class.getName());
    private final CartDAO cartDAO = new CartDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long userId = CartServlet.getUserId(request);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String productIdText = request.getParameter("productId");
        long productId;
        try {
            productId = Long.parseLong(productIdText);
            if (productId <= 0) {
                redirectToCart(request, response, "invalid-product");
                return;
            }
        } catch (NumberFormatException | NullPointerException exception) {
            redirectToCart(request, response, "invalid-product");
            return;
        }

        try {
            if (cartDAO.removeItem(userId, productId)) {
                response.sendRedirect(request.getContextPath() + "/cart?removed=1");
            } else {
                redirectToCart(request, response, "not-found");
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to remove cart item.", exception);
            redirectToCart(request, response, "database");
        }
    }

    private static void redirectToCart(HttpServletRequest request, HttpServletResponse response, String error)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/cart?error=" + error);
    }
}