package com.shopsphere.controller;

import com.shopsphere.dao.OrderDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/admin/orders")
public class AdminOrderListServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(AdminOrderListServlet.class.getName());
    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if ("updated".equals(request.getParameter("success"))) {
            request.setAttribute("successMessage", "Order status updated successfully.");
        }
        String error = request.getParameter("error");
        if ("not-found".equals(error)) {
            request.setAttribute("errorMessage", "That order could not be found.");
        } else if ("database".equals(error)) {
            request.setAttribute("errorMessage", "Orders are temporarily unavailable. Please try again.");
        }

        try {
            request.setAttribute("orders", orderDAO.findAllOrdersForAdmin());
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to load admin orders.", exception);
            request.setAttribute("errorMessage", "Orders are temporarily unavailable. Please try again.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        request.getRequestDispatcher("/WEB-INF/views/admin-orders.jsp").forward(request, response);
    }
}