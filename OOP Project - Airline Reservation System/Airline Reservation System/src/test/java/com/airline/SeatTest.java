package com.airline;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SeatTest {

    @Test
    void testSeatCreation() {
        Seat seat = new Seat(1, "1A", 101, true);

        assertEquals(1, seat.getId());
        assertEquals("1A", seat.getSeatNumber());
        assertEquals(101, seat.getFlightId());
        assertTrue(seat.isAvailable());
    }

    @Test
    void testSeatAvailability() {
        Seat seat = new Seat(2, "2B", 102, false);

        assertFalse(seat.isAvailable());
        seat.setAvailable(true);
        assertTrue(seat.isAvailable());
    }
}

