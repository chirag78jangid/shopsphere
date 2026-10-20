package com.shopsphere.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AdminOrderSummary {
    private final long id;
    private final String customerEmail;
    private final BigDecimal totalAmount;
    private final String status;
    private final String paymentStatus;
    private final String shippingAddress;
    private final LocalDateTime createdAt;

    public AdminOrderSummary(long id, String customerEmail, BigDecimal totalAmount, String status,
                             String paymentStatus, String shippingAddress, LocalDateTime createdAt) {
        this.id = id;
        this.customerEmail = customerEmail;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.shippingAddress = shippingAddress;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}