package com.shopsphere.controller;

import com.shopsphere.dao.ProductDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/admin/products/delete")
public class DeleteProductServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(DeleteProductServlet.class.getName());
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idText = request.getParameter("id");
        if (idText == null || idText.isBlank()) {
            redirectToList(request, response, "error", "invalid-id");
            return;
        }

        long productId;
        try {
            productId = Long.parseLong(idText);
            if (productId <= 0) {
                redirectToList(request, response, "error", "invalid-id");
                return;
            }
        } catch (NumberFormatException exception) {
            redirectToList(request, response, "error", "invalid-id");
            return;
        }

        try {
            if (productDAO.deleteProduct(productId)) {
                redirectToList(request, response, "success", "deleted");
            } else {
                redirectToList(request, response, "error", "not-found");
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.WARNING, "Unable to delete product " + productId + ".", exception);
            if (exception.getSQLState() != null && exception.getSQLState().startsWith("23")) {
                redirectToList(request, response, "error", "linked");
            } else {
                redirectToList(request, response, "error", "database");
            }
        }
    }

    private static void redirectToList(HttpServletRequest request, HttpServletResponse response,
                                       String parameter, String value) throws IOException {
        response.sendRedirect(request.getContextPath() + "/admin/products?" + parameter + "=" + value);
    }
}