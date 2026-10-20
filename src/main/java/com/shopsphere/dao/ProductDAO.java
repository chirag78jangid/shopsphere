package com.shopsphere.dao;

import com.shopsphere.model.Category;
import com.shopsphere.model.Product;
import com.shopsphere.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private static final String FIND_ACTIVE_PRODUCTS = """
            SELECT p.id, p.category_id, c.name AS category_name, p.name,
                   p.description, p.price, p.stock_quantity, p.image_url, p.active
            FROM products p
            LEFT JOIN categories c ON c.id = p.category_id
            WHERE p.active = TRUE
            ORDER BY p.name ASC
            """;
            private static final String FIND_ALL_PRODUCTS = """
                SELECT p.id, p.category_id, c.name AS category_name, p.name,
                   p.description, p.price, p.stock_quantity, p.image_url, p.active
                FROM products p
                LEFT JOIN categories c ON c.id = p.category_id
                ORDER BY p.name ASC
                """;
            private static final String FIND_PRODUCT_BY_ID = """
                SELECT p.id, p.category_id, c.name AS category_name, p.name,
                   p.description, p.price, p.stock_quantity, p.image_url, p.active
                FROM products p
                LEFT JOIN categories c ON c.id = p.category_id
                WHERE p.id = ?
                """;
            private static final String FIND_CATEGORIES = """
                SELECT id, name
                FROM categories
                ORDER BY name ASC
                """;
            private static final String INSERT_PRODUCT = """
                INSERT INTO products (category_id, name, description, price, stock_quantity, image_url)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
            private static final String UPDATE_PRODUCT = """
                UPDATE products
                SET category_id = ?, name = ?, description = ?, price = ?, stock_quantity = ?, image_url = ?
                WHERE id = ?
                """;
            private static final String DELETE_PRODUCT = "DELETE FROM products WHERE id = ?";

    public List<Product> findAllActiveProducts() throws SQLException {
        List<Product> products = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ACTIVE_PRODUCTS);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                products.add(readProduct(resultSet));
            }
        }

        return products;
    }

    public List<Product> findAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_PRODUCTS);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                products.add(readProduct(resultSet));
            }
        }

        return products;
    }

    public Product findProductById(long id) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_PRODUCT_BY_ID)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? readProduct(resultSet) : null;
            }
        }
    }

    public List<Category> findAllCategories() throws SQLException {
        List<Category> categories = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_CATEGORIES);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                categories.add(new Category(
                        resultSet.getLong("id"),
                        resultSet.getString("name")));
            }
        }

        return categories;
    }

    public boolean createProduct(Long categoryId, String name, String description,
                                 BigDecimal price, int stockQuantity, String imageUrl)
            throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_PRODUCT)) {
            statement.setLong(1, categoryId);
            statement.setString(2, name);
            statement.setString(3, description);
            statement.setBigDecimal(4, price);
            statement.setInt(5, stockQuantity);
            statement.setString(6, imageUrl);
            return statement.executeUpdate() == 1;
        }
    }

    public boolean updateProduct(long id, Long categoryId, String name, String description,
                                 BigDecimal price, int stockQuantity, String imageUrl)
            throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_PRODUCT)) {
            statement.setLong(1, categoryId);
            statement.setString(2, name);
            statement.setString(3, description);
            statement.setBigDecimal(4, price);
            statement.setInt(5, stockQuantity);
            statement.setString(6, imageUrl);
            statement.setLong(7, id);
            return statement.executeUpdate() == 1;
        }
    }

    public boolean deleteProduct(long id) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_PRODUCT)) {
            statement.setLong(1, id);
            return statement.executeUpdate() == 1;
        }
    }

    private static Product readProduct(ResultSet resultSet) throws SQLException {
        long categoryIdValue = resultSet.getLong("category_id");
        Long categoryId = resultSet.wasNull() ? null : categoryIdValue;

        return new Product(
                resultSet.getLong("id"),
                categoryId,
                resultSet.getString("category_name"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getBigDecimal("price"),
                resultSet.getInt("stock_quantity"),
                resultSet.getString("image_url"),
                resultSet.getBoolean("active"));
    }
}