package ru.practicum.shareit.booking.BookingStrategy;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class FutureBookingStrategy implements BookingStrategy {
    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> findBookingsByBookerId(Long userId) {
        return bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
    }

    @Override
    public List<Booking> findBookingsByOwnerId(Long userId) {
        return bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
    }
}
