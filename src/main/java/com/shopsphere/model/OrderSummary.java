package com.shopsphere.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderSummary {
    private final long id;
    private final BigDecimal totalAmount;
    private final String status;
    private final String shippingAddress;
    private final LocalDateTime createdAt;

    public OrderSummary(long id, BigDecimal totalAmount, String status,
                        String shippingAddress, LocalDateTime createdAt) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.status = status;
        this.shippingAddress = shippingAddress;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}