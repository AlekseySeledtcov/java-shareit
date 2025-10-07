package ru.practicum.shareit.booking.BookingStrategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AllBookingsStrategy implements BookingStrategy {
    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> findBookingsByBookerId(Long userId) {
        return bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
    }

    @Override
    public List<Booking> findBookingsByOwnerId(Long userId) {
        return bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
    }

    @Override
    public BookingState getState() {
        return BookingState.ALL;
    }
}
