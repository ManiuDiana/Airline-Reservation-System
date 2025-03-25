package com.airline;

public class Ticket {
    private int ticketId;
    private int passenger_id;
    private int flight_id;
    private String seatNumber;
    private double price;

    public Ticket(int ticketId, int passenger_id, int flight_id, String seatNumber, double price) {
        this.ticketId = ticketId;
        this.passenger_id = passenger_id;
        this.flight_id = flight_id;
        this.seatNumber = seatNumber;
        this.price = price;
    }

    public int getTicketId() {
        return ticketId;
    }

    public int getPassengerId() {
        return passenger_id;
    }

    public int getFlightId() {
        return flight_id;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "ticketId=" + ticketId +
                ", passenger=" + passenger_id +
                ", flight=" + flight_id +
                '}';
    }
}
