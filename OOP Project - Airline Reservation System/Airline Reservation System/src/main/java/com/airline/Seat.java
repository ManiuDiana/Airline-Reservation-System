package com.airline;

public class Seat {
    private int id;
    private String seatNumber;
    private int flightId;
    private boolean isAvailable;

    // Constructor
    public Seat(int id, String seatNumber, int flightId, boolean isAvailable) {
        this.id = id;
        this.seatNumber = seatNumber;
        this.flightId = flightId;
        this.isAvailable = isAvailable;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public int getFlightId() { return flightId; }
    public void setFlightId(int flightId) { this.flightId = flightId; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
}
