package com.shopsphere.dao;

import com.shopsphere.model.CartItem;
import com.shopsphere.model.Product;
import com.shopsphere.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {
    private static final String FIND_CART_ITEMS = """
            SELECT p.id, p.category_id, categories.name AS category_name, p.name,
                   p.description, p.price, p.stock_quantity, p.image_url, p.active,
                   cart.quantity AS cart_quantity
            FROM cart
            JOIN products p ON p.id = cart.product_id
            LEFT JOIN categories ON categories.id = p.category_id
            WHERE cart.user_id = ?
            ORDER BY cart.added_at DESC
            """;

    public List<CartItem> findCartItems(long userId) throws SQLException {
        List<CartItem> items = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_CART_ITEMS)) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    items.add(new CartItem(readProduct(resultSet), resultSet.getInt("cart_quantity")));
                }
            }
        }
        return items;
    }

    public CartResult addProduct(long userId, long productId, int quantity) throws SQLException {
        if (userId <= 0 || productId <= 0 || quantity < 1) {
            return CartResult.INVALID_QUANTITY;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                Integer stock = findStock(connection, productId, true);
                if (stock == null) {
                    connection.rollback();
                    return CartResult.PRODUCT_NOT_FOUND;
                }
                if (stock == 0) {
                    connection.rollback();
                    return CartResult.OUT_OF_STOCK;
                }

                Integer currentQuantity = findCartQuantity(connection, userId, productId);
                long newQuantity = (currentQuantity == null ? 0L : currentQuantity) + quantity;
                if (newQuantity > stock) {
                    connection.rollback();
                    return CartResult.EXCEEDS_STOCK;
                }

                if (currentQuantity == null) {
                    try (PreparedStatement statement = connection.prepareStatement(
                            "INSERT INTO cart (user_id, product_id, quantity) VALUES (?, ?, ?)")) {
                        statement.setLong(1, userId);
                        statement.setLong(2, productId);
                        statement.setLong(3, newQuantity);
                        statement.executeUpdate();
                    }
                } else {
                    updateQuantity(connection, userId, productId, (int) newQuantity);
                }

                connection.commit();
                return CartResult.SUCCESS;
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }
        }
    }

    public CartResult updateQuantity(long userId, long productId, int quantity) throws SQLException {
        if (userId <= 0 || productId <= 0 || quantity < 1) {
            return CartResult.INVALID_QUANTITY;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                Integer stock = findStock(connection, productId, false);
                if (stock == null) {
                    connection.rollback();
                    return CartResult.PRODUCT_NOT_FOUND;
                }
                if (findCartQuantity(connection, userId, productId) == null) {
                    connection.rollback();
                    return CartResult.CART_ITEM_NOT_FOUND;
                }
                if (quantity > stock) {
                    connection.rollback();
                    return CartResult.EXCEEDS_STOCK;
                }

                updateQuantity(connection, userId, productId, quantity);
                connection.commit();
                return CartResult.SUCCESS;
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }
        }
    }

    public boolean removeItem(long userId, long productId) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM cart WHERE user_id = ? AND product_id = ?")) {
            statement.setLong(1, userId);
            statement.setLong(2, productId);
            return statement.executeUpdate() == 1;
        }
    }

    private static Integer findStock(Connection connection, long productId, boolean activeOnly)
            throws SQLException {
        String sql = "SELECT stock_quantity FROM products WHERE id = ?" +
                (activeOnly ? " AND active = TRUE" : "") + " FOR UPDATE";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, productId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt("stock_quantity") : null;
            }
        }
    }

    private static Integer findCartQuantity(Connection connection, long userId, long productId)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT quantity FROM cart WHERE user_id = ? AND product_id = ? FOR UPDATE")) {
            statement.setLong(1, userId);
            statement.setLong(2, productId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt("quantity") : null;
            }
        }
    }

    private static void updateQuantity(Connection connection, long userId, long productId, int quantity)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE cart SET quantity = ? WHERE user_id = ? AND product_id = ?")) {
            statement.setInt(1, quantity);
            statement.setLong(2, userId);
            statement.setLong(3, productId);
            statement.executeUpdate();
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

    public enum CartResult {
        SUCCESS,
        PRODUCT_NOT_FOUND,
        CART_ITEM_NOT_FOUND,
        OUT_OF_STOCK,
        EXCEEDS_STOCK,
        INVALID_QUANTITY
    }
}