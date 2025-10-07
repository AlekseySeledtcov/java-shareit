package ru.practicum.shareit.booking.BookingStrategy;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingStrategy {
    List<Booking> findBookingsByBookerId(Long userId);

    List<Booking> findBookingsByOwnerId(Long userId);

    BookingState getState();
}
