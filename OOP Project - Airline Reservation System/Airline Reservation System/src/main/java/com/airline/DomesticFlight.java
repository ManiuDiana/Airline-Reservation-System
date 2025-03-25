package com.airline;

public class DomesticFlight extends Flight {
    public DomesticFlight(int flightId, String flightNumber, String departure, String destination, double basePrice, int availableSeats, String flightType) {
        super(flightId, flightNumber, departure, destination, basePrice, availableSeats, flightType);
    }

    @Override
    public double calculateTotalPrice(){
        return getBasePrice();
    }
}
