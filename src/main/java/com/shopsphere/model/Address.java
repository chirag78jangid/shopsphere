package com.shopsphere.model;

public class Address {
    private final long id;
    private final String addressLine;
    private final String city;
    private final String state;
    private final String postalCode;
    private final String country;
    private final boolean defaultAddress;

    public Address(long id, String addressLine, String city, String state,
                   String postalCode, String country, boolean defaultAddress) {
        this.id = id;
        this.addressLine = addressLine;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
        this.defaultAddress = defaultAddress;
    }

    public long getId() {
        return id;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountry() {
        return country;
    }

    public boolean isDefaultAddress() {
        return defaultAddress;
    }

    public String getFormattedAddress() {
        return String.join(", ", addressLine, city, state, postalCode, country);
    }
}