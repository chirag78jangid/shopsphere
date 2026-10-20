package com.shopsphere.controller;

import com.shopsphere.dao.ProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminProductListServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(AdminProductListServlet.class.getName());
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setMessage(request, "success", "added", "Product added successfully.");
        setMessage(request, "success", "updated", "Product updated successfully.");
        setMessage(request, "success", "deleted", "Product deleted successfully.");
        setMessage(request, "error", "invalid-id", "The product ID is invalid.");
        setMessage(request, "error", "not-found", "That product could not be found.");
        setMessage(request, "error", "linked", "This product is used by an order and cannot be deleted.");
        setMessage(request, "error", "database", "The product could not be deleted. Please try again.");

        try {
            request.setAttribute("products", productDAO.findAllProducts());
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to load admin products.", exception);
            request.setAttribute("errorMessage", "Products are temporarily unavailable. Please try again.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        request.getRequestDispatcher("/WEB-INF/views/admin-products.jsp").forward(request, response);
    }

    private static void setMessage(HttpServletRequest request, String parameter, String expectedValue,
                                   String message) {
        if (expectedValue.equals(request.getParameter(parameter))) {
            request.setAttribute(parameter + "Message", message);
        }
    }
}