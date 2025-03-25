package com.airline;

public class InternationalFlight extends Flight {
    private double internationalFee;

    public InternationalFlight(int flightId, String flightNumber, String departure, String destination, double basePrice, double internationalFee, int availableSeats, String flightType) {
        super(flightId, flightNumber, departure, destination, basePrice, availableSeats, flightType);
        this.internationalFee = internationalFee;
    }

    @Override
    public String toString() {
        return super.toString() + " | Intl Fee: $" + internationalFee;
    }

    public double calculateTotalPrice() {
        // Total price includes base price and international fee
        return getBasePrice() + internationalFee;
    }

}
