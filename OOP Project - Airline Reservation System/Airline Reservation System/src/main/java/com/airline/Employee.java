package com.airline;

public class Employee extends Account {
    private String department;
    private String position;
    private String fullName;

    public Employee(int accountId, String username, String password, String phoneNumber, String email,
                    String department, String position, String fullName) {
        super(accountId, username, password, phoneNumber, email);
        this.department = department;
        this.position = position;
        this.fullName = fullName;
    }

    // Getters and Setters
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String getRole() {
        return "Employee";
    }
}

