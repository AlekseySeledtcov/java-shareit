package ru.practicum.shareit.booking.BookingStrategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RejectedBookingStrategy implements BookingStrategy {
    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> findBookingsByBookerId(Long userId) {
        return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
    }

    @Override
    public List<Booking> findBookingsByOwnerId(Long userId) {
        return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
    }

    @Override
    public BookingState getState() {
        return BookingState.REJECTED;
    }
}
