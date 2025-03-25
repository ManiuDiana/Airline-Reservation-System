package com.airline;

import java.util.regex.Pattern;

public abstract class Account {
    private int accountId;
    private String username;
    private String password;
    private String phoneNumber;
    private String email;

    // Constructor
    public Account(int accountId, String username, String password, String phoneNumber, String email) {
        this.accountId = accountId;
        this.username = username;
        this.password = password;
        setPhoneNumber(phoneNumber); // Apply validation
        setEmail(email); // Apply validation
    }

    // Getters and Setters
    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        // Validate phone number (must be exactly 10 digits)
        if (phoneNumber == null || !phoneNumber.matches("\\d{10}")) {
            throw new IllegalArgumentException("Phone number must be exactly 10 digits.");
        }
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        // Validate email format using a regex
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (email == null || !Pattern.matches(emailRegex, email)) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        this.email = email;
    }

    // Abstract method for specific roles
    public abstract String getRole();
}
