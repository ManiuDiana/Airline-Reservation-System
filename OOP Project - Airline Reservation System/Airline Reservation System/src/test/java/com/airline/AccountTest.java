package com.airline;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void testUserCreation() {
        User user = new User(1, "user123", "password", "1234567890", "user@example.com", "John Doe", "P123456");

        assertEquals(1, user.getAccountId());
        assertEquals("user123", user.getUsername());
        assertEquals("John Doe", user.getFullName());
        assertEquals("P123456", user.getPassportNumber());
    }

    @Test
    void testEmployeeCreation() {
        Employee employee = new Employee(2, "employee456", "securepass", "9876543210", "employee@example.com", "IT", "Manager", "Mary Jane");

        assertEquals(2, employee.getAccountId());
        assertEquals("employee456", employee.getUsername());
        assertEquals("IT", employee.getDepartment());
        assertEquals("Manager", employee.getPosition());
    }
}

