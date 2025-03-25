package com.airline;

import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionTest {

    @Test
    void testFetchFlights() throws Exception {
        List<Flight> flights = DatabaseConnection.fetchFlights();
        assertNotNull(flights, "Flights list should not be null");
        assertTrue(flights.size() > 0, "Flights list should not be empty");
    }

    @Test
    void testFetchPassengers() throws SQLException {
        List<Passenger> passengers = DatabaseConnection.fetchPassengers();
        assertNotNull(passengers, "Passengers list should not be null");
        assertTrue(passengers.size() > 0, "Passengers list should not be empty");
    }

    @Test
    void testFetchTickets() throws SQLException {
        List<Ticket> tickets = DatabaseConnection.fetchTickets();
        assertNotNull(tickets, "Tickets list should not be null");
        assertTrue(tickets.size() > 0, "Tickets list should not be empty");
    }

    @Test
    void testAddFlight() throws SQLException {
        boolean result = DatabaseConnection.addFlight(
                "TEST123",
                "Test Destination",
                "Test Departure",
                100.0,
                10
        );
        assertTrue(result, "Flight should be added successfully");
    }

    @Test
    void testDeleteFlight() throws SQLException {
        boolean result = DatabaseConnection.deleteFlight(1); // Replace with a valid flight ID
        assertTrue(result, "Flight should be deleted successfully");
    }

    @Test
    void testAddSeat() throws SQLException {
        Seat seat = new Seat(0, "1A", 1, true); // Replace with a valid flight ID
        DatabaseConnection.addSeat(seat);
        List<Seat> seats = DatabaseConnection.fetchSeatsByFlight(1);
        assertTrue(seats.stream().anyMatch(s -> s.getSeatNumber().equals("1A")),
                "Seat should be added successfully");
    }

    @Test
    void testRemoveSeat() throws SQLException {
        boolean result = DatabaseConnection.removeSeat(1); // Replace with a valid seat ID
        assertTrue(result, "Seat should be removed successfully");
    }

    @Test
    void testUpdateSeatAvailability() throws SQLException {
        boolean result = DatabaseConnection.updateSeatAvailability(1, false); // Replace with a valid seat ID
        assertTrue(result, "Seat availability should be updated successfully");
    }

    @Test
    void testIsSeatAvailable() throws SQLException {
        boolean isAvailable = DatabaseConnection.isSeatAvailable("1A", 1); // Replace with valid seat and flight IDs
        assertTrue(isAvailable, "Seat should be available");
    }

    @Test
    void testReserveSeatAndDecrementAvailable() throws SQLException {
        boolean result = DatabaseConnection.reserveSeatAndDecrementAvailable("1A", 1, "TEST123"); // Replace with valid data
        assertTrue(result, "Seat should be reserved and availability decremented");
    }

    @Test
    void testAddUserAccount() throws SQLException {
        boolean result = DatabaseConnection.addUserAccount(
                "testUser",
                "password123",
                "1234567890",
                "test@example.com",
                "Test Full Name",
                "P123456"
        );
        assertTrue(result, "User account should be added successfully");
    }

    @Test
    void testAddEmployeeAccount() throws SQLException {
        boolean result = DatabaseConnection.addEmployeeAccount(
                "testEmployee",
                "securePass",
                "0987654321",
                "employee@example.com",
                "Manager",
                "IT Department",
                "Test Employee"
        );
        assertTrue(result, "Employee account should be added successfully");
    }

    @Test
    void testGetAccountByCredentials() throws SQLException {
        Account account = DatabaseConnection.getAccountByCredentials("testUser", "password123");
        assertNotNull(account, "Account should be fetched successfully");
        assertEquals("testUser", account.getUsername(), "Fetched account username should match");
    }

    @Test
    void testDeleteAccount() throws SQLException {
        boolean result = DatabaseConnection.deleteAccount("testUser");
        assertTrue(result, "Account should be deleted successfully");
    }
}