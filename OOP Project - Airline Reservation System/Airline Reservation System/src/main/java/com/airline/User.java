package com.airline;

public class User extends Account {
    private String fullName;
    private String passportNumber;

    public User(int accountId, String username, String password, String phoneNumber, String email,
                String fullName, String passportNumber) {
        super(accountId, username, password, phoneNumber, email);
        this.fullName = fullName;
        this.passportNumber = passportNumber;
    }

    // Getters and Setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    @Override
    public String getRole() {
        return "User";
    }
}
