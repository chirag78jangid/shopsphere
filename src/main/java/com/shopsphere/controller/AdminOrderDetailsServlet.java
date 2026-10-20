package com.shopsphere.controller;

import com.shopsphere.dao.OrderDAO;
import com.shopsphere.dao.OrderDAO.OrderStatus;
import com.shopsphere.model.AdminOrderSummary;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/admin/orders/view")
public class AdminOrderDetailsServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(AdminOrderDetailsServlet.class.getName());
    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long orderId = parsePositiveLong(request.getParameter("id"));
        if (orderId == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String error = request.getParameter("error");
        if ("invalid-status".equals(error)) {
            request.setAttribute("errorMessage", "Select a valid order status.");
        } else if ("database".equals(error)) {
            request.setAttribute("errorMessage", "The order status could not be updated. Please try again.");
        }
        request.setAttribute("statusUpdated", "1".equals(request.getParameter("updated")));

        try {
            AdminOrderSummary order = orderDAO.findOrderById(orderId);
            if (order == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            request.setAttribute("order", order);
            request.setAttribute("orderItems", orderDAO.findItemsForOrder(orderId));
            request.setAttribute("orderStatuses", Arrays.stream(OrderStatus.values())
                    .map(Enum::name)
                    .toList());
            request.getRequestDispatcher("/WEB-INF/views/admin-order-details.jsp").forward(request, response);
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to load admin order details.", exception);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long orderId = parsePositiveLong(request.getParameter("id"));
        if (orderId == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        OrderStatus status = OrderStatus.fromValue(request.getParameter("status"));
        if (status == null) {
            redirectToDetails(request, response, orderId, "error=invalid-status");
            return;
        }

        try {
            if (orderDAO.updateOrderStatus(orderId, status)) {
                redirectToDetails(request, response, orderId, "updated=1");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/orders?error=not-found");
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to update admin order status.", exception);
            redirectToDetails(request, response, orderId, "error=database");
        }
    }

    private static Long parsePositiveLong(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            long orderId = Long.parseLong(value);
            return orderId > 0 ? orderId : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private static void redirectToDetails(HttpServletRequest request, HttpServletResponse response,
                                          long orderId, String result) throws IOException {
        response.sendRedirect(request.getContextPath() + "/admin/orders/view?id=" + orderId + "&" + result);
    }
}