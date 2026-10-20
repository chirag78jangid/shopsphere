package com.shopsphere.dao;

import com.shopsphere.model.Address;
import com.shopsphere.model.AdminOrderSummary;
import com.shopsphere.model.OrderItem;
import com.shopsphere.model.OrderSummary;
import com.shopsphere.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    public List<Address> findAddressesForUser(long userId) throws SQLException {
        List<Address> addresses = new ArrayList<>();
        String sql = """
                SELECT id, address_line, city, state, postal_code, country, is_default
                FROM addresses
                WHERE user_id = ?
                ORDER BY is_default DESC, id DESC
                """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    addresses.add(readAddress(resultSet));
                }
            }
        }
        return addresses;
    }

    public PlacementResult placeOrder(long userId, Long addressId, Address newAddress) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                if (!customerExists(connection, userId)) {
                    connection.rollback();
                    return PlacementResult.failure(PlacementStatus.USER_NOT_FOUND);
                }

                List<CheckoutLine> lines = lockCartLines(connection, userId);
                if (lines.isEmpty()) {
                    connection.rollback();
                    return PlacementResult.failure(PlacementStatus.EMPTY_CART);
                }

                BigDecimal total = BigDecimal.ZERO;
                for (CheckoutLine line : lines) {
                    if (!line.active || line.quantity > line.stockQuantity) {
                        connection.rollback();
                        return PlacementResult.failure(PlacementStatus.INSUFFICIENT_STOCK);
                    }
                    total = total.add(line.unitPrice.multiply(BigDecimal.valueOf(line.quantity)));
                }
                total = total.setScale(2);
                if (total.precision() - total.scale() > 8) {
                    connection.rollback();
                    return PlacementResult.failure(PlacementStatus.TOTAL_TOO_LARGE);
                }

                String shippingAddress = resolveAddress(connection, userId, addressId, newAddress);
                if (shippingAddress == null || shippingAddress.length() > 500) {
                    connection.rollback();
                    return PlacementResult.failure(PlacementStatus.INVALID_ADDRESS);
                }
                if (addressId == null && !saveAddress(connection, userId, newAddress)) {
                    connection.rollback();
                    return PlacementResult.failure(PlacementStatus.INVALID_ADDRESS);
                }

                long orderId = insertOrder(connection, userId, total, shippingAddress);
                for (CheckoutLine line : lines) {
                    if (!decrementStock(connection, line.productId, line.quantity)) {
                        connection.rollback();
                        return PlacementResult.failure(PlacementStatus.INSUFFICIENT_STOCK);
                    }
                    insertOrderItem(connection, orderId, line);
                }
                insertPayment(connection, orderId, total);

                if (clearCart(connection, userId) != lines.size()) {
                    connection.rollback();
                    return PlacementResult.failure(PlacementStatus.CART_CHANGED);
                }

                connection.commit();
                return PlacementResult.success(orderId, total);
            } catch (SQLException | RuntimeException exception) {
                connection.rollback();
                throw exception;
            }
        }
    }

    public List<OrderSummary> findOrdersForUser(long userId) throws SQLException {
        List<OrderSummary> orders = new ArrayList<>();
        String sql = """
                SELECT id, total_amount, status, shipping_address, created_at
                FROM orders
                WHERE user_id = ?
                ORDER BY created_at DESC, id DESC
                """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    orders.add(readOrder(resultSet));
                }
            }
        }
        return orders;
    }

    public List<AdminOrderSummary> findAllOrdersForAdmin() throws SQLException {
        List<AdminOrderSummary> orders = new ArrayList<>();
        String sql = """
                SELECT o.id, u.email AS customer_email, o.total_amount, o.status,
                       p.status AS payment_status, o.shipping_address, o.created_at
                FROM orders o
                JOIN users u ON u.id = o.user_id
                LEFT JOIN payments p ON p.order_id = o.id
                ORDER BY o.created_at DESC, o.id DESC
                """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                orders.add(readAdminOrder(resultSet));
            }
        }
        return orders;
    }

    public AdminOrderSummary findOrderById(long orderId) throws SQLException {
        String sql = """
                SELECT o.id, u.email AS customer_email, o.total_amount, o.status,
                       p.status AS payment_status, o.shipping_address, o.created_at
                FROM orders o
                JOIN users u ON u.id = o.user_id
                LEFT JOIN payments p ON p.order_id = o.id
                WHERE o.id = ?
                """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, orderId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? readAdminOrder(resultSet) : null;
            }
        }
    }

    public boolean updateOrderStatus(long orderId, OrderStatus status) throws SQLException {
        if (orderId <= 0 || status == null) {
            return false;
        }
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE orders SET status = ? WHERE id = ?")) {
            statement.setString(1, status.name());
            statement.setLong(2, orderId);
            return statement.executeUpdate() == 1;
        }
    }

    public OrderSummary findOrderForUser(long userId, long orderId) throws SQLException {
        String sql = """
                SELECT id, total_amount, status, shipping_address, created_at
                FROM orders
                WHERE id = ? AND user_id = ?
                """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, orderId);
            statement.setLong(2, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? readOrder(resultSet) : null;
            }
        }
    }

    public List<OrderItem> findItemsForOrder(long orderId) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        String sql = """
                SELECT product_name, quantity, unit_price
                FROM order_items
                WHERE order_id = ?
                ORDER BY id ASC
                """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, orderId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    items.add(new OrderItem(
                            resultSet.getString("product_name"),
                            resultSet.getInt("quantity"),
                            resultSet.getBigDecimal("unit_price")));
                }
            }
        }
        return items;
    }

    private static boolean customerExists(Connection connection, long userId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT id FROM users WHERE id = ? AND role = 'CUSTOMER' FOR UPDATE")) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private static List<CheckoutLine> lockCartLines(Connection connection, long userId) throws SQLException {
        List<CheckoutLine> lines = new ArrayList<>();
        String sql = """
                SELECT p.id, p.name, p.price, p.stock_quantity, p.active, cart.quantity
                FROM cart
                JOIN products p ON p.id = cart.product_id
                WHERE cart.user_id = ?
                ORDER BY p.id
                FOR UPDATE
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    lines.add(new CheckoutLine(
                            resultSet.getLong("id"),
                            resultSet.getString("name"),
                            resultSet.getBigDecimal("price"),
                            resultSet.getInt("stock_quantity"),
                            resultSet.getBoolean("active"),
                            resultSet.getInt("quantity")));
                }
            }
        }
        return lines;
    }

    private static String resolveAddress(Connection connection, long userId, Long addressId,
                                         Address newAddress) throws SQLException {
        if (addressId == null) {
            if (newAddress == null || !validAddress(newAddress)) {
                return null;
            }
            return newAddress.getFormattedAddress();
        }

        String sql = """
                SELECT id, address_line, city, state, postal_code, country, is_default
                FROM addresses
                WHERE id = ? AND user_id = ?
                FOR UPDATE
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, addressId);
            statement.setLong(2, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? readAddress(resultSet).getFormattedAddress() : null;
            }
        }
    }

    private static boolean validAddress(Address address) {
        return !address.getAddressLine().isBlank() && address.getAddressLine().length() <= 255
                && !address.getCity().isBlank() && address.getCity().length() <= 100
                && !address.getState().isBlank() && address.getState().length() <= 100
                && !address.getPostalCode().isBlank() && address.getPostalCode().length() <= 20
                && !address.getCountry().isBlank() && address.getCountry().length() <= 100;
    }

    private static boolean saveAddress(Connection connection, long userId, Address address)
            throws SQLException {
        String sql = """
                INSERT INTO addresses (user_id, address_line, city, state, postal_code, country, is_default)
                VALUES (?, ?, ?, ?, ?, ?, FALSE)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.setString(2, address.getAddressLine());
            statement.setString(3, address.getCity());
            statement.setString(4, address.getState());
            statement.setString(5, address.getPostalCode());
            statement.setString(6, address.getCountry());
            return statement.executeUpdate() == 1;
        }
    }

    private static long insertOrder(Connection connection, long userId, BigDecimal total,
                                    String shippingAddress) throws SQLException {
        String sql = "INSERT INTO orders (user_id, total_amount, shipping_address) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, userId);
            statement.setBigDecimal(2, total);
            statement.setString(3, shippingAddress);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        throw new SQLException("Order insert did not return a generated ID.");
    }

    private static boolean decrementStock(Connection connection, long productId, int quantity)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE products SET stock_quantity = stock_quantity - ? " +
                        "WHERE id = ? AND active = TRUE AND stock_quantity >= ?")) {
            statement.setInt(1, quantity);
            statement.setLong(2, productId);
            statement.setInt(3, quantity);
            return statement.executeUpdate() == 1;
        }
    }

    private static void insertOrderItem(Connection connection, long orderId, CheckoutLine line)
            throws SQLException {
        String sql = """
                INSERT INTO order_items (order_id, product_id, product_name, quantity, unit_price)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, orderId);
            statement.setLong(2, line.productId);
            statement.setString(3, line.productName);
            statement.setInt(4, line.quantity);
            statement.setBigDecimal(5, line.unitPrice);
            statement.executeUpdate();
        }
    }

    private static void insertPayment(Connection connection, long orderId, BigDecimal total)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO payments (order_id, amount, method, status) VALUES (?, ?, 'COD', 'PENDING')")) {
            statement.setLong(1, orderId);
            statement.setBigDecimal(2, total);
            statement.executeUpdate();
        }
    }

    private static int clearCart(Connection connection, long userId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM cart WHERE user_id = ?")) {
            statement.setLong(1, userId);
            return statement.executeUpdate();
        }
    }

    private static Address readAddress(ResultSet resultSet) throws SQLException {
        return new Address(
                resultSet.getLong("id"),
                resultSet.getString("address_line"),
                resultSet.getString("city"),
                resultSet.getString("state"),
                resultSet.getString("postal_code"),
                resultSet.getString("country"),
                resultSet.getBoolean("is_default"));
    }

    private static OrderSummary readOrder(ResultSet resultSet) throws SQLException {
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        LocalDateTime createdAtValue = createdAt == null ? null : createdAt.toLocalDateTime();
        return new OrderSummary(
                resultSet.getLong("id"),
                resultSet.getBigDecimal("total_amount"),
                resultSet.getString("status"),
                resultSet.getString("shipping_address"),
                createdAtValue);
    }

    private static AdminOrderSummary readAdminOrder(ResultSet resultSet) throws SQLException {
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        LocalDateTime createdAtValue = createdAt == null ? null : createdAt.toLocalDateTime();
        return new AdminOrderSummary(
                resultSet.getLong("id"),
                resultSet.getString("customer_email"),
                resultSet.getBigDecimal("total_amount"),
                resultSet.getString("status"),
                resultSet.getString("payment_status"),
                resultSet.getString("shipping_address"),
                createdAtValue);
    }

    public enum OrderStatus {
        PENDING,
        CONFIRMED,
        SHIPPED,
        DELIVERED,
        CANCELLED;

        public static OrderStatus fromValue(String value) {
            if (value == null) {
                return null;
            }
            try {
                return valueOf(value);
            } catch (IllegalArgumentException exception) {
                return null;
            }
        }
    }

    private record CheckoutLine(long productId, String productName, BigDecimal unitPrice,
                                int stockQuantity, boolean active, int quantity) {
    }

    public enum PlacementStatus {
        SUCCESS,
        USER_NOT_FOUND,
        EMPTY_CART,
        INVALID_ADDRESS,
        INSUFFICIENT_STOCK,
        TOTAL_TOO_LARGE,
        CART_CHANGED
    }

    public record PlacementResult(PlacementStatus status, long orderId, BigDecimal total) {
        private static PlacementResult failure(PlacementStatus status) {
            return new PlacementResult(status, 0, BigDecimal.ZERO);
        }

        private static PlacementResult success(long orderId, BigDecimal total) {
            return new PlacementResult(PlacementStatus.SUCCESS, orderId, total);
        }
    }
}