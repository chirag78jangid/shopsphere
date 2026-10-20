package com.shopsphere.controller;

import com.shopsphere.dao.ProductDAO;
import com.shopsphere.model.Category;
import com.shopsphere.model.Product;
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

@WebServlet("/admin/products/edit")
public class EditProductServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(EditProductServlet.class.getName());
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long productId = parseId(request.getParameter("id"));
        if (productId == null) {
            redirectToList(request, response, "error", "invalid-id");
            return;
        }

        try {
            Product product = productDAO.findProductById(productId);
            if (product == null) {
                redirectToList(request, response, "error", "not-found");
                return;
            }
            request.setAttribute("product", product);
            request.setAttribute("categories", productDAO.findAllCategories());
            setFormValues(request, product.getName(), product.getDescription(),
                    product.getPrice().toPlainString(), String.valueOf(product.getStockQuantity()),
                    product.getCategoryId() == null ? "" : String.valueOf(product.getCategoryId()),
                    product.getImageUrl());
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to load product for editing.", exception);
            redirectToList(request, response, "error", "database");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/views/edit-product.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Long productId = parseId(request.getParameter("id"));
        if (productId == null) {
            redirectToList(request, response, "error", "invalid-id");
            return;
        }

        String name = value(request, "name").trim();
        String description = value(request, "description").trim();
        String priceText = value(request, "price").trim();
        String stockText = value(request, "stockQuantity").trim();
        String categoryText = value(request, "categoryId").trim();
        String imageUrl = value(request, "imageUrl").trim();
        setFormValues(request, name, description, priceText, stockText, categoryText, imageUrl);

        List<Category> categories;
        Product product;
        try {
            categories = productDAO.findAllCategories();
            product = productDAO.findProductById(productId);
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to load product edit data.", exception);
            redirectToList(request, response, "error", "database");
            return;
        }
        if (product == null) {
            redirectToList(request, response, "error", "not-found");
            return;
        }
        request.setAttribute("product", product);
        request.setAttribute("categories", categories);

        String validationError = AddProductServlet.validate(
                name, priceText, stockText, categoryText, imageUrl, categories);
        if (validationError != null) {
            request.setAttribute("errorMessage", validationError);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.getRequestDispatcher("/WEB-INF/views/edit-product.jsp").forward(request, response);
            return;
        }

        BigDecimal price = new BigDecimal(priceText).setScale(2, RoundingMode.UNNECESSARY);
        int stockQuantity = Integer.parseInt(stockText);
        long categoryId = Long.parseLong(categoryText);
        try {
            if (productDAO.updateProduct(productId, categoryId, name,
                    AddProductServlet.emptyAsNull(description), price, stockQuantity,
                    AddProductServlet.emptyAsNull(imageUrl))) {
                redirectToList(request, response, "success", "updated");
                return;
            }
            redirectToList(request, response, "error", "not-found");
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to update product.", exception);
            request.setAttribute("errorMessage", "The product could not be updated. Check the category and try again.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.getRequestDispatcher("/WEB-INF/views/edit-product.jsp").forward(request, response);
        }
    }

    private static Long parseId(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            long id = Long.parseLong(value);
            return id > 0 ? id : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private static String value(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null ? "" : value;
    }

    private static void setFormValues(HttpServletRequest request, String name, String description,
                                      String price, String stockQuantity, String categoryId, String imageUrl) {
        request.setAttribute("productName", name == null ? "" : name);
        request.setAttribute("description", description == null ? "" : description);
        request.setAttribute("price", price == null ? "" : price);
        request.setAttribute("stockQuantity", stockQuantity == null ? "" : stockQuantity);
        request.setAttribute("selectedCategoryId", categoryId == null ? "" : categoryId);
        request.setAttribute("imageUrl", imageUrl == null ? "" : imageUrl);
    }

    private static void redirectToList(HttpServletRequest request, HttpServletResponse response,
                                        String parameter, String value) throws IOException {
        response.sendRedirect(request.getContextPath() + "/admin/products?" + parameter + "=" + value);
    }
}