package com.shopsphere.controller;

import com.shopsphere.dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(RegisterServlet.class.getName());
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String fullName = value(request, "fullName").trim();
        String email = value(request, "email").trim().toLowerCase(Locale.ROOT);
        String phone = value(request, "phone").trim();
        String password = value(request, "password");
        String confirmPassword = value(request, "confirmPassword");

        request.setAttribute("fullName", fullName);
        request.setAttribute("email", email);
        request.setAttribute("phone", phone);

        String validationError = validate(fullName, email, phone, password, confirmPassword);
        if (validationError != null) {
            showError(request, response, validationError);
            return;
        }

        try {
            if (userDAO.findByEmail(email) != null) {
                showError(request, response, "An account with this email already exists.");
                return;
            }
            userDAO.createUser(fullName, email, password, phone.isEmpty() ? null : phone);
            response.sendRedirect(request.getContextPath() + "/login?registered=1");
        } catch (SQLException exception) {
            if (exception.getSQLState() != null && exception.getSQLState().startsWith("23")) {
                showError(request, response, "An account with this email already exists.");
                return;
            }
            LOGGER.log(Level.SEVERE, "Unable to create a ShopSphere account.", exception);
            showError(request, response, "Registration is temporarily unavailable. Please try again.");
        }
    }

    private static String validate(String fullName, String email, String phone,
                                   String password, String confirmPassword) {
        if (fullName.isEmpty() || fullName.length() > 120) {
            return "Enter your name using no more than 120 characters.";
        }
        if (email.length() > 190 || !EMAIL_PATTERN.matcher(email).matches()) {
            return "Enter a valid email address (maximum 190 characters).";
        }
        if (phone.length() > 30) {
            return "Phone number must be 30 characters or fewer.";
        }
        if (password.length() < 8 || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            return "Password must be at least 8 characters and no more than 72 UTF-8 bytes.";
        }
        if (!password.equals(confirmPassword)) {
            return "The passwords do not match.";
        }
        return null;
    }

    private static String value(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null ? "" : value;
    }

    private static void showError(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute("errorMessage", message);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
    }
}