package com.shopsphere.controller;

import com.shopsphere.dao.UserDAO;
import com.shopsphere.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if ("1".equals(request.getParameter("registered"))) {
            request.setAttribute("successMessage", "Your account was created. You can now log in.");
        }
        if ("1".equals(request.getParameter("loggedOut"))) {
            request.setAttribute("successMessage", "You have been logged out.");
        }
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String email = value(request, "email").trim().toLowerCase(Locale.ROOT);
        String password = value(request, "password");
        request.setAttribute("email", email);

        if (email.length() > 190 || !EMAIL_PATTERN.matcher(email).matches()
                || password.isEmpty() || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            showError(request, response, "Enter a valid email address and password.");
            return;
        }

        try {
            User user = userDAO.findByEmail(email);
            if (user == null || !BCrypt.checkpw(password, user.getPasswordHash())) {
                showError(request, response, "Email or password is incorrect.");
                return;
            }

            HttpSession existingSession = request.getSession(false);
            if (existingSession != null) {
                existingSession.invalidate();
            }
            HttpSession session = request.getSession(true);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userEmail", user.getEmail());
            session.setAttribute("userRole", user.getRole());
            response.sendRedirect(request.getContextPath() + "/");
        } catch (java.sql.SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to authenticate a ShopSphere user.", exception);
            showError(request, response, "Login is temporarily unavailable. Please try again.");
        }
    }

    private static String value(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null ? "" : value;
    }

    private static void showError(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute("errorMessage", message);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }
}