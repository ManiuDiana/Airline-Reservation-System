package com.airline;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel(String imagePath) {
        try {
            backgroundImage = new ImageIcon(imagePath).getImage();
        } catch (Exception e) {
            e.printStackTrace();
            backgroundImage = null;
        }
        setLayout(null); // Allow absolute positioning for child components
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

public class GUI {
    private JFrame frame;
    private JComboBox<Flight> comboFlights;
    private JLabel labelResult;

    public GUI() {
        initializeGUI();
    }

    private void initializeGUI() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame = new JFrame("Airline Reservation System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        //frame.setLayout(new FlowLayout(FlowLayout.CENTER));
        frame.setLayout(new BorderLayout());

/////////////////// Top Panel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("Airline Reservation System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(3, 78, 154));
        topPanel.add(titleLabel);
        frame.add(topPanel, BorderLayout.NORTH);

////////////////////// Main Panel with Background
        JPanel mainPanel = new BackgroundPanel("C:/Facultate/OOP - Proiecte/MavenExample/src/main/resources/Plane.jpg"); // Provide your image path here
        mainPanel.setLayout(null); // Retain the layout


        ///Creation of the buttons
        JLabel labelFlight = new JLabel("Select Flight:");
        labelFlight.setBounds(50, 30, 150, 25);
        labelFlight.setFont(new Font("Arial", Font.PLAIN, 14));
        labelFlight.setForeground(new Color(255, 253, 253));
        mainPanel.add(labelFlight);

        comboFlights = new JComboBox<>();
        comboFlights.setBounds(50, 60, 500, 35);
        comboFlights.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(comboFlights);

        JButton buttonFetchFlights = new JButton("Load Flights");
        buttonFetchFlights.setBounds(580, 60, 120, 30);
        mainPanel.add(buttonFetchFlights);

        labelResult = new JLabel("");
        labelResult.setBounds(50, 100, 500, 25);
        labelResult.setFont(new Font("Arial", Font.ITALIC, 12));
        labelResult.setForeground(new Color(206, 255, 118));
        mainPanel.add(labelResult);

        JButton buttonViewSeats = new JButton("View Seats");
        buttonViewSeats.setBounds(50, 140, 150, 30);
        mainPanel.add(buttonViewSeats);

        JButton buttonReserveSeat = new JButton("Reserve Seat");
        buttonReserveSeat.setBounds(220, 140, 150, 30);
        mainPanel.add(buttonReserveSeat);

        JButton buttonAdminPanel = new JButton("Admin Panel");
        buttonAdminPanel.setFont(new Font("Arial", Font.BOLD, 14));
        buttonAdminPanel.setBounds(195, 430, 200, 40);
        buttonAdminPanel.setBackground(new Color(208, 183, 255));
        buttonAdminPanel.setForeground(Color.BLACK);
        mainPanel.add(buttonAdminPanel);

        //buttons to display tables
        JButton buttonSeePassengers = new JButton("See Passengers");
        buttonSeePassengers.setBounds(50, 310, 150, 40);
        styleButton(buttonSeePassengers);
        mainPanel.add(buttonSeePassengers);

        JButton buttonSeeTickets = new JButton("See Tickets");
        buttonSeeTickets.setBounds(220, 310, 150, 40);
        styleButton(buttonSeeTickets);
        mainPanel.add(buttonSeeTickets);

        JButton buttonSeeFlights = new JButton("See Flights");
        buttonSeeFlights.setBounds(390, 310, 150, 40);
        styleButton(buttonSeeFlights);
        mainPanel.add(buttonSeeFlights);

        JButton buttonManageAccounts = new JButton("Accounts");
        buttonManageAccounts.setFont(new Font("Arial", Font.BOLD, 14));
        buttonManageAccounts.setBounds(195, 380, 200, 40);
        buttonManageAccounts.setBackground(new Color(208, 183, 255));
        mainPanel.add(buttonManageAccounts);

        // Add main panel
        frame.add(mainPanel, BorderLayout.CENTER);


///////////////////// Footer Panel
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel footerLabel = new JLabel("© Maniu Diana 2025 Airline Reservation System");
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        footerLabel.setForeground(new Color(128, 128, 128));
        footerPanel.add(footerLabel);
        frame.add(footerPanel, BorderLayout.SOUTH);

/// /// /// ///BUTTON ACTIONS
        // Load flights from database
        buttonFetchFlights.addActionListener(e -> loadFlights());

        // View seats for a selected flight
        buttonViewSeats.addActionListener(e -> displaySeats());

        // Reserve a specific seat
        buttonReserveSeat.addActionListener(e -> reserveSeat());

        // Admin Panel Access with Password
        buttonAdminPanel.addActionListener(e -> {
            String password = JOptionPane.showInputDialog(frame, "Enter Admin Password:");
            if ("admin123".equals(password)) { // Replace with your desired password
                new AdminPanel().display();
            } else {
                JOptionPane.showMessageDialog(frame, "Incorrect password. Access denied.");
            }
        });

        // Action listener to open the account management dialog
        buttonManageAccounts.addActionListener(e -> openAccountManagementDialog());


        //Display of tables
        buttonSeePassengers.addActionListener(e -> displayPassengers());
        buttonSeeTickets.addActionListener(e -> displayTickets());
        buttonSeeFlights.addActionListener(e -> displayFlights());


        frame.setVisible(true);
    }

    //Method to load the list of flights from the database
    private void loadFlights() {
        try {
            List<Flight> flights = DatabaseConnection.fetchFlights();
            comboFlights.removeAllItems();
            for (Flight flight : flights) {
                comboFlights.addItem(flight);
            }
            labelResult.setText("Flights loaded successfully.");
        } catch (Exception ex) {
            labelResult.setText("Failed to load flights: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    //Method to reserve a seat
    private void reserveSeat() {
        Flight selectedFlight = (Flight) comboFlights.getSelectedItem();
        if (selectedFlight == null) {
            labelResult.setText("No flight selected.");
            return;
        }

        String seatNumber = JOptionPane.showInputDialog("Enter Seat Number to Reserve:");
        if (seatNumber == null || seatNumber.trim().isEmpty()) {
            labelResult.setText("Seat number not entered.");
            return;
        }

        try {
            // Attempt to reserve the seat and decrement available seats atomically
            boolean success = DatabaseConnection.reserveSeatAndDecrementAvailable(
                    seatNumber.trim(), selectedFlight.getFlightId(), selectedFlight.getFlightNumber()
            );

            if (success) {
                labelResult.setText("Seat reserved successfully: " + seatNumber);
                loadFlights(); // Refresh the flights list to show updated availability
            } else {
                labelResult.setText("Reservation failed. Seat may already be reserved or flight is full.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error reserving seat: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    ///Display tables
    private void displayPassengers() {
        try {
            List<Passenger> passengers = DatabaseConnection.fetchPassengers();
            String[] columnNames = {"ID", "Name", "Passport Number"};
            Object[][] tableData = new Object[passengers.size()][3];

            for (int i = 0; i < passengers.size(); i++) {
                Passenger passenger = passengers.get(i);
                tableData[i] = new Object[]{
                        passenger.getId(),
                        passenger.getName(),
                        passenger.getPassportNumber()
                };
            }

            JTable table = new JTable(tableData, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            JFrame frame = new JFrame("Passengers List");
            frame.setSize(600, 400);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.add(scrollPane);
            frame.setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error fetching passengers: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void displayTickets() {
        try {
            List<Ticket> tickets = DatabaseConnection.fetchTickets();
            String[] columnNames = {"ID", "Passenger ID", "Flight ID", "Seat Number", "Price"};
            Object[][] tableData = new Object[tickets.size()][5];

            for (int i = 0; i < tickets.size(); i++) {
                Ticket ticket = tickets.get(i);
                tableData[i] = new Object[]{
                        ticket.getTicketId(),
                        ticket.getPassengerId(),
                        ticket.getFlightId(),
                        ticket.getSeatNumber(),
                        ticket.getPrice()
                };
            }

            JTable table = new JTable(tableData, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            JFrame frame = new JFrame("Tickets List");
            frame.setSize(800, 400);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.add(scrollPane);
            frame.setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error fetching tickets: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void displayFlights() {
        try {
            List<Flight> flights = DatabaseConnection.fetchFlights();
            String[] columnNames = {"Flight ID", "Flight Number", "Destination", "Departure", "Base Price", "Available Seats", "Flyght Type"};
            Object[][] tableData = new Object[flights.size()][7];

            for (int i = 0; i < flights.size(); i++) {
                Flight flight = flights.get(i);
                tableData[i] = new Object[]{
                        flight.getFlightId(),
                        flight.getFlightNumber(),
                        flight.getDestination(),
                        flight.getDeparture(),
                        flight.getBasePrice(),
                        flight.getAvailableSeats(),
                        flight.getFlightType()
                };
            }

            JTable table = new JTable(tableData, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            JFrame frame = new JFrame("Flights List");
            frame.setSize(800, 400);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.add(scrollPane);
            frame.setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error fetching flights: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void displaySeats() {
        Flight selectedFlight = (Flight) comboFlights.getSelectedItem();
        if (selectedFlight == null) {
            labelResult.setText("No flight selected.");
            return;
        }

        try {
            List<Seat> seats = DatabaseConnection.fetchSeatsByFlight(selectedFlight.getFlightId());
            System.out.println("Fetched seats: " + seats.size());
            for (Seat seat : seats) {
                System.out.println("Seat: " + seat.getSeatNumber() + ", Available: " + seat.isAvailable());
            }

            if (seats.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No seats available for this flight.");
                return;
            }

            String[] columnNames = {"Seat ID", "Seat Number", "Flight ID", "Available"};
            Object[][] tableData = new Object[seats.size()][4];

            for (int i = 0; i < seats.size(); i++) {
                Seat seat = seats.get(i);
                tableData[i] = new Object[]{
                        seat.getId(),
                        seat.getSeatNumber(),
                        seat.getFlightId(),
                        seat.isAvailable() ? "Yes" : "No"
                };
            }

            JTable table = new JTable(tableData, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);

            JFrame seatFrame = new JFrame("Seats for Flight: " + selectedFlight.getFlightNumber());
            seatFrame.setSize(600, 400);
            seatFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            seatFrame.add(scrollPane);
            seatFrame.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error fetching seats: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void openAccountManagementDialog() {
        JFrame accountFrame = new JFrame("Manage Accounts");
        accountFrame.setSize(600, 500);
        accountFrame.setLayout(null); // Use null layout for absolute positioning

        // Input Fields
        JLabel labelUsername = new JLabel("Username:");
        labelUsername.setBounds(50, 30, 150, 25);
        JTextField textUsername = new JTextField();
        textUsername.setBounds(200, 30, 300, 25);

        JLabel labelPassword = new JLabel("Password:");
        labelPassword.setBounds(50, 70, 150, 25);
        JPasswordField textPassword = new JPasswordField();
        textPassword.setBounds(200, 70, 300, 25);

        JLabel labelPhone = new JLabel("Phone Number:");
        labelPhone.setBounds(50, 110, 150, 25);
        JTextField textPhone = new JTextField();
        textPhone.setBounds(200, 110, 300, 25);

        JLabel labelEmail = new JLabel("Email:");
        labelEmail.setBounds(50, 150, 150, 25);
        JTextField textEmail = new JTextField();
        textEmail.setBounds(200, 150, 300, 25);

        JLabel labelRole = new JLabel("Role:");
        labelRole.setBounds(50, 190, 150, 25);
        JComboBox<String> comboRole = new JComboBox<>(new String[]{"User", "Employee"});
        comboRole.setBounds(200, 190, 300, 25);

        // User-Specific Fields
        JLabel labelFullName = new JLabel("Full Name:");
        labelFullName.setBounds(50, 230, 150, 25);
        JTextField textFullName = new JTextField();
        textFullName.setBounds(200, 230, 300, 25);

        JLabel labelPassport = new JLabel("Passport Number:");
        labelPassport.setBounds(50, 270, 150, 25);
        JTextField textPassport = new JTextField();
        textPassport.setBounds(200, 270, 300, 25);

        // Employee-Specific Fields
        JLabel labelPosition = new JLabel("Position:");
        labelPosition.setBounds(50, 310, 150, 25);
        JTextField textPosition = new JTextField();
        textPosition.setBounds(200, 310, 300, 25);

        JLabel labelDepartment = new JLabel("Department:");
        labelDepartment.setBounds(50, 350, 150, 25);
        JTextField textDepartment = new JTextField();
        textDepartment.setBounds(200, 350, 300, 25);

        // Buttons
        JButton buttonAddAccount = new JButton("Add Account");
        buttonAddAccount.setBounds(50, 400, 150, 30);

        JButton buttonDeleteAccount = new JButton("Delete Account");
        buttonDeleteAccount.setBounds(220, 400, 150, 30);

        JButton buttonViewAccount = new JButton("View Account");
        buttonViewAccount.setBounds(390, 400, 150, 30);

        // Add components to the frame
        accountFrame.add(labelUsername);
        accountFrame.add(textUsername);
        accountFrame.add(labelPassword);
        accountFrame.add(textPassword);
        accountFrame.add(labelPhone);
        accountFrame.add(textPhone);
        accountFrame.add(labelEmail);
        accountFrame.add(textEmail);
        accountFrame.add(labelRole);
        accountFrame.add(comboRole);

        accountFrame.add(labelFullName);
        accountFrame.add(textFullName);
        accountFrame.add(labelPassport);
        accountFrame.add(textPassport);
        accountFrame.add(labelPosition);
        accountFrame.add(textPosition);
        accountFrame.add(labelDepartment);
        accountFrame.add(textDepartment);

        accountFrame.add(buttonAddAccount);
        accountFrame.add(buttonDeleteAccount);
        accountFrame.add(buttonViewAccount);

        // Initially hide role-specific fields
        labelFullName.setVisible(false);
        textFullName.setVisible(false);
        labelPassport.setVisible(false);
        textPassport.setVisible(false);

        labelPosition.setVisible(false);
        textPosition.setVisible(false);
        labelDepartment.setVisible(false);
        textDepartment.setVisible(false);

        // Show/Hide Fields Based on Role Selection
        comboRole.addActionListener(e -> {
            String role = (String) comboRole.getSelectedItem();
            boolean isUser = "User".equals(role);

            // User fields
            labelFullName.setVisible(isUser);
            textFullName.setVisible(isUser);
            labelPassport.setVisible(isUser);
            textPassport.setVisible(isUser);

            // Employee fields
            labelFullName.setVisible(!isUser);
            textFullName.setVisible(!isUser);
            labelPosition.setVisible(!isUser);
            textPosition.setVisible(!isUser);
            labelDepartment.setVisible(!isUser);
            textDepartment.setVisible(!isUser);
        });

        // Button Actions
        buttonAddAccount.addActionListener(e -> {
            String username = textUsername.getText();
            String password = new String(textPassword.getPassword());
            String phone = textPhone.getText();
            String email = textEmail.getText();
            String role = (String) comboRole.getSelectedItem();
            String fullName = textFullName.getText();
            String passport = textPassport.getText();
            String position = textPosition.getText();
            String department = textDepartment.getText();

            if (username.isEmpty() || password.isEmpty() || phone.isEmpty() || email.isEmpty() || role.isEmpty()) {
                JOptionPane.showMessageDialog(accountFrame, "All fields must be filled out!");
                return;
            }

            try {
                boolean success;
                if ("User".equals(role)) {
                    success = DatabaseConnection.addUserAccount(username, password, phone, email, fullName, passport);
                } else {
                    success = DatabaseConnection.addEmployeeAccount(username, password, phone, email, position, department, fullName);
                }

                if (success) {
                    JOptionPane.showMessageDialog(accountFrame, "Account added successfully!");
                    // Clear fields
                    textUsername.setText("");
                    textPassword.setText("");
                    textPhone.setText("");
                    textEmail.setText("");
                    textFullName.setText("");
                    textPassport.setText("");
                    textPosition.setText("");
                    textDepartment.setText("");
                } else {
                    JOptionPane.showMessageDialog(accountFrame, "Failed to add account. Please try again.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(accountFrame, "Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        buttonViewAccount.addActionListener(e -> {
            String username = textUsername.getText();
            String password = new String(textPassword.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(accountFrame, "Enter username and password to view account.");
                return;
            }

            try {
                Account account = DatabaseConnection.getAccountByCredentials(username, password);
                if (account != null) {
                    JOptionPane.showMessageDialog(accountFrame, "Account Details:\n" + account);
                    // Clear fields
                    textUsername.setText("");
                    textPassword.setText("");
                    textPhone.setText("");
                    textEmail.setText("");
                    textFullName.setText("");
                    textPassport.setText("");
                    textPosition.setText("");
                    textDepartment.setText("");
                } else {
                    JOptionPane.showMessageDialog(accountFrame, "No account found with the given credentials.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(accountFrame, "Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        buttonDeleteAccount.addActionListener(e -> {
            String username = textUsername.getText();
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(accountFrame, "Enter the username to delete the account.");
                return;
            }

            try {
                if (DatabaseConnection.deleteAccount(username)) {
                    JOptionPane.showMessageDialog(accountFrame, "Account deleted successfully!");
                    // Clear fields
                    textUsername.setText("");
                    textPassword.setText("");
                    textPhone.setText("");
                    textEmail.setText("");
                    textFullName.setText("");
                    textPassport.setText("");
                    textPosition.setText("");
                    textDepartment.setText("");
                } else {
                    JOptionPane.showMessageDialog(accountFrame, "No account found with the given username.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(accountFrame, "Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });


        accountFrame.setVisible(true);
    }

    // Method to style buttons uniformly
    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBackground(new Color(133, 195, 234));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(150, 40));
    }

}


