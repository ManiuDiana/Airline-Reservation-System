package com.airline;

import java.sql.*;
import java.util.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.airline.DatabaseConnection;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/airlinesystem";
    private static final String USER = "postgres";
    private static final String PASSWORD = "diana0808";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    ///For displaying the tables
    public static List<Flight> fetchFlights() throws Exception {
        List<Flight> flights = new ArrayList<>();
        String query = "SELECT * FROM flights";

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int flightId = resultSet.getInt("id");
                String flightNumber = resultSet.getString("flight_number");
                String departure = resultSet.getString("departure");
                String destination = resultSet.getString("destination");
                double basePrice = resultSet.getDouble("base_price");
                int availableSeats = resultSet.getInt("available_seats");
                String flightType = resultSet.getString("flightType");

                Flight flight;
                if (flightNumber.startsWith("IF")) { // Identify international flights by prefix
                    double internationalFee = 100.0; // Example fixed fee for international flights
                    flight = new InternationalFlight(flightId, flightNumber, departure, destination, basePrice, internationalFee, availableSeats, flightType);
                } else {
                    flight = new DomesticFlight(flightId, flightNumber, departure, destination, basePrice, availableSeats, flightType);
                }
                flights.add(flight);
            }
        }
        return flights;
    }

    public static List<Passenger> fetchPassengers() throws SQLException {
        List<Passenger> passengers = new ArrayList<>();
        String query = "SELECT * FROM passengers";

        try (Connection connection = DatabaseConnection.connect();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Passenger passenger = new Passenger(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("passport_number")
                );
                passengers.add(passenger);
            }
        }
        return passengers;
    }

    public static List<Ticket> fetchTickets() throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        String query = "SELECT * FROM tickets";

        try (Connection connection = DatabaseConnection.connect();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Ticket ticket = new Ticket(
                        rs.getInt("id"),
                        rs.getInt("passenger_id"),
                        rs.getInt("flight_id"),
                        rs.getString("seat_number"),
                        rs.getDouble("price")
                );
                tickets.add(ticket);
            }
        }
        return tickets;
    }

    /// Seat Management Functionalities
    public static boolean isSeatAvailable(String seatNumber, int flightId) throws SQLException {
        String query = "SELECT isAvailable FROM seats WHERE seatNumber = ? AND flightId = ?";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, seatNumber);
            statement.setInt(2, flightId);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("isAvailable");
            }
        }
        return false; // Return false if the seat does not exist
    }

    public static boolean reserveSeatAndDecrementAvailable(String seatNumber, int flightId, String flightNumber) throws SQLException {
        String checkSeatQuery = "SELECT isavailable FROM seats WHERE seatnumber = ? AND flightid = ?";
        String updateSeatQuery = "UPDATE seats SET isavailable = false WHERE seatnumber = ? AND flightid = ? AND isavailable = true";
        String decrementSeatsQuery = "UPDATE flights SET available_seats = available_seats - 1 WHERE flight_number = ? AND available_seats > 0";

        Connection connection = null;
        PreparedStatement checkSeatStmt = null;
        PreparedStatement updateSeatStmt = null;
        PreparedStatement decrementSeatsStmt = null;

        try {
            connection = connect();
            connection.setAutoCommit(false); // Start transaction

            // Check if the seat is available
            checkSeatStmt = connection.prepareStatement(checkSeatQuery);
            checkSeatStmt.setString(1, seatNumber);
            checkSeatStmt.setInt(2, flightId);
            ResultSet resultSet = checkSeatStmt.executeQuery();

            if (resultSet.next()) {
                boolean isAvailable = resultSet.getBoolean("isavailable");

                if (!isAvailable) {
                    // Seat is already reserved
                    connection.rollback();
                    return false;
                }
            } else {
                // Seat doesn't exist for the given flight
                connection.rollback();
                throw new SQLException("Seat not found for the given flight ID.");
            }

            // Update the seat to mark it as reserved
            updateSeatStmt = connection.prepareStatement(updateSeatQuery);
            updateSeatStmt.setString(1, seatNumber);
            updateSeatStmt.setInt(2, flightId);

            int seatUpdated = updateSeatStmt.executeUpdate();
            if (seatUpdated == 0) {
                // Seat reservation failed
                connection.rollback();
                return false;
            }

            // Decrement available seats in the flights table
            decrementSeatsStmt = connection.prepareStatement(decrementSeatsQuery);
            decrementSeatsStmt.setString(1, flightNumber);

            int seatsDecremented = decrementSeatsStmt.executeUpdate();
            if (seatsDecremented == 0) {
                // No available seats left to decrement
                connection.rollback();
                return false;
            }

            // Commit transaction if both operations succeeded
            connection.commit();
            return true;
        } catch (SQLException ex) {
            if (connection != null) {
                connection.rollback(); // Roll back transaction on failure
            }
            throw ex;
        } finally {
            if (checkSeatStmt != null) checkSeatStmt.close();
            if (updateSeatStmt != null) updateSeatStmt.close();
            if (decrementSeatsStmt != null) decrementSeatsStmt.close();
            if (connection != null) {
                connection.setAutoCommit(true); // Restore default commit behavior
                connection.close();
            }
        }
    }

    public static List<Seat> fetchSeatsByFlight(int flightId) throws SQLException {
        List<Seat> seats = new ArrayList<>();
        String query = "SELECT * FROM seats WHERE flightid = ?";

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, flightId);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String seatNumber = rs.getString("seatNumber");
                boolean isAvailable = rs.getBoolean("isAvailable");
                seats.add(new Seat(id, seatNumber, flightId, isAvailable));
            }
        }
        return seats;
    }

    ///This is for the admin
    public static void addSeat(Seat seat) throws SQLException {
        String getNextIdQuery = "SELECT COALESCE(MAX(id), 0) + 1 FROM seats";
        String insertQuery = "INSERT INTO seats (id, seatnumber, flightid, isavailable) VALUES (?, ?, ?, ?)";

        try (Connection connection = connect();
             PreparedStatement getIdStatement = connection.prepareStatement(getNextIdQuery);
             PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {

            // Get the next available ID
            ResultSet resultSet = getIdStatement.executeQuery();
            int nextId = 1; // Default to 1 if table is empty
            if (resultSet.next()) {
                nextId = resultSet.getInt(1);
            }

            // Insert the new seat
            insertStatement.setInt(1, nextId); // Set the ID
            insertStatement.setString(2, seat.getSeatNumber());
            insertStatement.setInt(3, seat.getFlightId());
            insertStatement.setBoolean(4, seat.isAvailable());

            insertStatement.executeUpdate();
        }
    }

    public static boolean removeSeat(int seatId) throws SQLException {
        String query = "DELETE FROM seats WHERE id = ?";

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, seatId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0; // Return true if the seat was deleted
        }
    }

    public static boolean updateSeatAvailability(int seatId, boolean isAvailable) throws SQLException {
        String query = "UPDATE seats SET isavailable = ? WHERE id = ?";

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setBoolean(1, isAvailable);
            statement.setInt(2, seatId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0; // Return true if the seat was updated
        }
    }

    public static boolean addFlight(String flightNumber, String destination, String departure, double basePrice, int availableSeats) throws SQLException {
        String getNextIdQuery = "SELECT COALESCE(MAX(id), 0) + 1 FROM flights";
        String insertQuery = "INSERT INTO flights (id, flight_number, destination, departure, base_price, available_seats) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = connect();
             PreparedStatement getIdStatement = connection.prepareStatement(getNextIdQuery);
             PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {

            // Get the next available ID
            ResultSet resultSet = getIdStatement.executeQuery();
            int nextId = 1; // Default to 1 if table is empty
            if (resultSet.next()) {
                nextId = resultSet.getInt(1);
            }

            // Insert the new flight
            insertStatement.setInt(1, nextId); // Set the ID
            insertStatement.setString(2, flightNumber);
            insertStatement.setString(3, destination);
            insertStatement.setString(4, departure);
            insertStatement.setDouble(5, basePrice);
            insertStatement.setInt(6, availableSeats);

            return insertStatement.executeUpdate() > 0;
        }
    }

    public static boolean deleteFlight(int flightId) throws SQLException {
        String query = "DELETE FROM flights WHERE id = ?";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, flightId);
            return statement.executeUpdate() > 0;
        }
    }

//    public static boolean addAccount(String username, String password, String phoneNumber, String email, String role) throws SQLException {
//        String getNextIdQuery = "SELECT COALESCE(MAX(account_id), 0) + 1 FROM accounts";
//        String insertQuery = "INSERT INTO accounts (account_id, username, password, phone_number, email, role) VALUES (?, ?, ?, ?, ?, ?)";
//
//        try (Connection connection = connect();
//             PreparedStatement getIdStatement = connection.prepareStatement(getNextIdQuery);
//             PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
//
//            // Get the next available ID
//            ResultSet resultSet = getIdStatement.executeQuery();
//            int nextId = 1; // Default to 1 if the table is empty
//            if (resultSet.next()) {
//                nextId = resultSet.getInt(1);
//            }
//
//            // Insert the new account
//            insertStatement.setInt(1, nextId); // Set the unique ID
//            insertStatement.setString(2, username);
//            insertStatement.setString(3, password);
//            insertStatement.setString(4, phoneNumber);
//            insertStatement.setString(5, email);
//            insertStatement.setString(6, role);
//
//            return insertStatement.executeUpdate() > 0; // Return true if the insert is successful
//        }
//    }

    public static boolean deleteAccount(String username) throws SQLException {
        String query = "DELETE FROM accounts WHERE username = ?";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            return statement.executeUpdate() > 0;
        }
    }

    public static boolean addEmployeeAccount(String username, String password, String phone, String email, String position, String department, String fullName) throws SQLException {
        String getNextIdQuery = "SELECT COALESCE(MAX(account_id), 0) + 1 FROM accounts";
        String insertQuery = "INSERT INTO accounts (account_id, username, password, phone_number, email, role, position, department, full_name) VALUES (?, ?, ?, ?, ?, 'Employee', ?, ?, ?)";

        try (Connection connection = connect();
             PreparedStatement getIdStatement = connection.prepareStatement(getNextIdQuery);
             PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {

            // Get the next available account_id
            ResultSet resultSet = getIdStatement.executeQuery();
            int nextId = 1; // Default to 1 if the table is empty
            if (resultSet.next()) {
                nextId = resultSet.getInt(1);
            }

            // Insert the new employee account
            insertStatement.setInt(1, nextId); // Set the unique ID
            insertStatement.setString(2, username);
            insertStatement.setString(3, password);
            insertStatement.setString(4, phone);
            insertStatement.setString(5, email);
            insertStatement.setString(6, position);
            insertStatement.setString(7, department);
            insertStatement.setString(8, fullName);

            return insertStatement.executeUpdate() > 0;
        }
    }

    public static boolean addUserAccount(String username, String password, String phone, String email, String fullName, String passport) throws SQLException {
        String getNextIdQuery = "SELECT COALESCE(MAX(account_id), 0) + 1 FROM accounts";
        String insertQuery = "INSERT INTO accounts (account_id, username, password, phone_number, email, role, full_name, passport_number) VALUES (?, ?, ?, ?, ?, 'User', ?, ?)";

        try (Connection connection = connect();
             PreparedStatement getIdStatement = connection.prepareStatement(getNextIdQuery);
             PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {

            // Get the next available account_id
            ResultSet resultSet = getIdStatement.executeQuery();
            int nextId = 1; // Default to 1 if the table is empty
            if (resultSet.next()) {
                nextId = resultSet.getInt(1);
            }

            // Insert the new user account
            insertStatement.setInt(1, nextId); // Set the unique ID
            insertStatement.setString(2, username);
            insertStatement.setString(3, password);
            insertStatement.setString(4, phone);
            insertStatement.setString(5, email);
            insertStatement.setString(6, fullName);
            insertStatement.setString(7, passport);

            return insertStatement.executeUpdate() > 0;
        }
    }

    public static Account getAccountByCredentials(String username, String password) throws SQLException {
        String query = "SELECT * FROM accounts WHERE username = ? AND password = ?";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String role = resultSet.getString("role");
                if ("User".equals(role)) {
                    return new User(
                            resultSet.getInt("account_id"),
                            resultSet.getString("username"),
                            resultSet.getString("password"),
                            resultSet.getString("phone_number"),
                            resultSet.getString("email"),
                            resultSet.getString("full_name"),
                            resultSet.getString("passport_number")
                    );
                } else if ("Employee".equals(role)) {
                    return new Employee(
                            resultSet.getInt("account_id"),
                            resultSet.getString("username"),
                            resultSet.getString("password"),
                            resultSet.getString("phone_number"),
                            resultSet.getString("email"),
                            resultSet.getString("department"),
                            resultSet.getString("position"),
                            resultSet.getString("full_name")
                    );
                }
            }
            return null; // No account found
        }
    }


}
