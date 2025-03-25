package com.airline;

public abstract class Flight {
    protected int flightId;
    protected String flightNumber;
    protected String departure;
    protected String destination;
    protected double basePrice;
    protected int availableSeats;
    protected String flightType;

    public Flight(int flightId, String flightNumber, String departure, String destination, double basePrice, int availableSeats, String flightType) {
        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.departure = departure;
        this.destination = destination;
        this.basePrice = basePrice;
        this.availableSeats = availableSeats;
        this.flightType = flightType;
    }

    public int getFlightId() {
        return flightId;
    }

    public String getFlightType(){
        return flightType;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public String getDeparture() {
        return departure;
    }

    public String getDestination() {
        return destination;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public abstract double calculateTotalPrice();

    @Override
    public String toString() {
        return flightNumber + " | " + departure + " -> " + destination + " | $" + basePrice + " | Seats: " + availableSeats;
    }
}
