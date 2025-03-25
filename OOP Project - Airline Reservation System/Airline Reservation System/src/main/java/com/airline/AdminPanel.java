package com.airline;

import javax.swing.*;
import java.awt.*;

public class AdminPanel {

    public void display() {
//        // Prompt for admin password
//        String password = JOptionPane.showInputDialog(null, "Enter Admin Password:", "Admin Login", JOptionPane.PLAIN_MESSAGE);
//
//        // Verify the password (simple hardcoded example)
//        if (password == null || !password.equals("admin123")) {
//            JOptionPane.showMessageDialog(null, "Incorrect password. Access denied.", "Error", JOptionPane.ERROR_MESSAGE);
//            return;
//        }

        // Admin panel UI setup
        JFrame frame = new JFrame("Admin Panel");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        JButton addButton = new JButton("Add Flight");
        JButton deleteButton = new JButton("Delete Flight");
        JButton addSeatButton = new JButton("Add Seat");
        JButton removeSeatButton = new JButton("Remove Seat");
        JButton updateSeatAvailabilityButton = new JButton("Update Seat Availability");
        JButton backButton = new JButton("Back");

        panel.add(addButton);
        panel.add(deleteButton);
        panel.add(addSeatButton);
        panel.add(removeSeatButton);
        panel.add(updateSeatAvailabilityButton);
        panel.add(backButton);


        //Add / Delete Flight Functionalities
        addButton.addActionListener(e -> addFlight());
        deleteButton.addActionListener(e -> deleteFlight());

        //Seats Functionalities
        addSeatButton.addActionListener(e -> handleAddSeat(frame));
        removeSeatButton.addActionListener(e -> handleRemoveSeat(frame));
        updateSeatAvailabilityButton.addActionListener(e -> handleUpdateSeatAvailability(frame));
        backButton.addActionListener(e -> frame.dispose());

        // Back Button to Close Admin Panel
        backButton.addActionListener(e -> frame.dispose());


        frame.add(panel);
        frame.setVisible(true);
    }

    private void addFlight() {
        JTextField flightNumberField = new JTextField();
        JTextField destinationField = new JTextField();
        JTextField departureField = new JTextField();
        JTextField basePriceField = new JTextField();
        JTextField availableSeatsField = new JTextField();

        Object[] fields = {
                "Flight Number:", flightNumberField,
                "Destination:", destinationField,
                "Departure:", departureField,
                "Base Price:", basePriceField,
                "Available Seats:", availableSeatsField
        };

        int option = JOptionPane.showConfirmDialog(null, fields, "Add Flight", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String flightNumber = flightNumberField.getText().trim();
            String destination = destinationField.getText().trim();
            String departure = departureField.getText().trim();
            String basePrice = basePriceField.getText().trim();
            String availableSeats = availableSeatsField.getText().trim();

            if (!flightNumber.isEmpty() && !destination.isEmpty() && !departure.isEmpty() && !basePrice.isEmpty() && !availableSeats.isEmpty()) {
                try {
                    boolean success = DatabaseConnection.addFlight(flightNumber, destination, departure, Double.parseDouble(basePrice), Integer.parseInt(availableSeats));
                    if (success) {
                        JOptionPane.showMessageDialog(null, "Flight added successfully!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to add flight.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteFlight() {
        String flightId = JOptionPane.showInputDialog(null, "Enter Flight ID to Delete:");

        if (flightId == null || flightId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Flight ID is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            boolean success = DatabaseConnection.deleteFlight(Integer.parseInt(flightId));

            if (success) {
                JOptionPane.showMessageDialog(null, "Flight deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to delete flight.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to handle adding a seat
    private void handleAddSeat(JFrame frame) {
        try {
            String seatNumber = JOptionPane.showInputDialog(frame, "Enter Seat Number:");
            if (seatNumber == null || seatNumber.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Seat Number cannot be empty.");
                return;
            }

            String flightIdString = JOptionPane.showInputDialog(frame, "Enter Flight ID:");
            if (flightIdString == null || flightIdString.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Flight ID cannot be empty.");
                return;
            }

            int flightId = Integer.parseInt(flightIdString);

            boolean isAvailable = JOptionPane.showConfirmDialog(
                    frame,
                    "Is the seat available?",
                    "Seat Availability",
                    JOptionPane.YES_NO_OPTION
            ) == JOptionPane.YES_OPTION;

            // Add seat to database
            Seat seat = new Seat(0, seatNumber, flightId, isAvailable); // 0 for ID since it's auto-generated
            DatabaseConnection.addSeat(seat);

            JOptionPane.showMessageDialog(frame, "Seat added successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error adding seat: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Method to handle removing a seat
    private void handleRemoveSeat(JFrame frame) {
        try {
            String seatIdString = JOptionPane.showInputDialog(frame, "Enter Seat ID to Remove:");
            if (seatIdString == null || seatIdString.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Seat ID cannot be empty.");
                return;
            }

            int seatId = Integer.parseInt(seatIdString);

            // Remove seat from database
            boolean success = DatabaseConnection.removeSeat(seatId);
            if (success) {
                JOptionPane.showMessageDialog(frame, "Seat removed successfully!");
            } else {
                JOptionPane.showMessageDialog(frame, "Seat not found.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error removing seat: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Method to handle updating seat availability
    private void handleUpdateSeatAvailability(JFrame frame) {
        try {
            String seatIdString = JOptionPane.showInputDialog(frame, "Enter Seat ID to Update:");
            if (seatIdString == null || seatIdString.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Seat ID cannot be empty.");
                return;
            }

            int seatId = Integer.parseInt(seatIdString);

            boolean isAvailable = JOptionPane.showConfirmDialog(
                    frame,
                    "Is the seat available?",
                    "Seat Availability",
                    JOptionPane.YES_NO_OPTION
            ) == JOptionPane.YES_OPTION;

            // Update seat availability in database
            boolean success = DatabaseConnection.updateSeatAvailability(seatId, isAvailable);
            if (success) {
                JOptionPane.showMessageDialog(frame, "Seat availability updated successfully!");
            } else {
                JOptionPane.showMessageDialog(frame, "Seat not found.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error updating seat availability: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}

