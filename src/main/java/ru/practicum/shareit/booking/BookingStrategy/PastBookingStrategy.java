package ru.practicum.shareit.booking.BookingStrategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PastBookingStrategy implements BookingStrategy {
    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> findBookingsByBookerId(Long userId) {
        return bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
    }

    @Override
    public List<Booking> findBookingsByOwnerId(Long userId) {
        return bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
    }

    @Override
    public BookingState getState() {
        return BookingState.PAST;
    }
}
