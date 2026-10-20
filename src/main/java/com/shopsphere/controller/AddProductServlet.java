package com.shopsphere.controller;

import com.shopsphere.dao.ProductDAO;
import com.shopsphere.model.Category;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/admin/products/new")
public class AddProductServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(AddProductServlet.class.getName());
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        showForm(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String name = value(request, "name").trim();
        String description = value(request, "description").trim();
        String priceText = value(request, "price").trim();
        String stockText = value(request, "stockQuantity").trim();
        String categoryText = value(request, "categoryId").trim();
        String imageUrl = value(request, "imageUrl").trim();

        preserveFormValues(request, name, description, priceText, stockText, categoryText, imageUrl);

        List<Category> categories;
        try {
            categories = productDAO.findAllCategories();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to load product categories.", exception);
            request.setAttribute("errorMessage", "Categories are temporarily unavailable. Please try again.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            showForm(request, response);
            return;
        }
        request.setAttribute("categories", categories);

        String validationError = validate(name, priceText, stockText, categoryText, imageUrl, categories);
        if (validationError != null) {
            request.setAttribute("errorMessage", validationError);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            forwardToForm(request, response);
            return;
        }

        BigDecimal price = new BigDecimal(priceText).setScale(2, RoundingMode.UNNECESSARY);
        int stockQuantity = Integer.parseInt(stockText);
        long categoryId = Long.parseLong(categoryText);
        try {
            if (productDAO.createProduct(categoryId, name, emptyAsNull(description), price,
                    stockQuantity, emptyAsNull(imageUrl))) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard?section=product-added");
                return;
            }
            request.setAttribute("errorMessage", "The product could not be saved. Please try again.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to save a product.", exception);
            request.setAttribute("errorMessage", "The product could not be saved. Check the category and try again.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        forwardToForm(request, response);
    }

    private void showForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("categories", productDAO.findAllCategories());
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to load product categories.", exception);
            request.setAttribute("errorMessage", "Categories are temporarily unavailable. Please try again.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        forwardToForm(request, response);
    }

    private static void forwardToForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/add-product.jsp").forward(request, response);
    }

    static String validate(String name, String priceText, String stockText, String categoryText,
                           String imageUrl, List<Category> categories) {
        if (name.isEmpty() || name.length() > 180) {
            return "Product name is required and must be 180 characters or fewer.";
        }

        try {
            BigDecimal price = new BigDecimal(priceText);
            if (price.signum() <= 0) {
                return "Price must be greater than 0.";
            }
            if (price.scale() > 2 || price.precision() - price.scale() > 8) {
                return "Enter a price with no more than 2 decimal places and at most 8 digits before the decimal.";
            }
        } catch (NumberFormatException exception) {
            return "Enter a valid price greater than 0.";
        }

        try {
            if (Integer.parseInt(stockText) < 0) {
                return "Stock quantity must be 0 or greater.";
            }
        } catch (NumberFormatException exception) {
            return "Enter a valid whole-number stock quantity.";
        }

        try {
            long categoryId = Long.parseLong(categoryText);
            boolean categoryExists = categories.stream().anyMatch(category -> category.getId() == categoryId);
            if (!categoryExists) {
                return "Select a valid category.";
            }
        } catch (NumberFormatException exception) {
            return "Select a category.";
        }

        if (imageUrl.length() > 500) {
            return "Image URL must be 500 characters or fewer.";
        }
        return null;
    }

    private static void preserveFormValues(HttpServletRequest request, String name, String description,
                                           String price, String stockQuantity, String categoryId, String imageUrl) {
        request.setAttribute("productName", name);
        request.setAttribute("description", description);
        request.setAttribute("price", price);
        request.setAttribute("stockQuantity", stockQuantity);
        request.setAttribute("selectedCategoryId", categoryId);
        request.setAttribute("imageUrl", imageUrl);
    }

    private static String value(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null ? "" : value;
    }

    static String emptyAsNull(String value) {
        return value.isEmpty() ? null : value;
    }
}