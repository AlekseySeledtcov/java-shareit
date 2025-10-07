package ru.practicum.shareit.booking.BookingStrategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.exceptions.BadRequestException;

import java.util.ArrayList;
import java.util.List;

@Component
public class BookingStrategyFactory {

    @Autowired
    private final List<BookingStrategy> strategies = new ArrayList<>();

    public BookingStrategy getBookingStrategy(BookingState bookingState) {
        return strategies.stream()
                .filter(bookingStrategy -> bookingStrategy.getState() == bookingState)
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Для состояния " + bookingState + " стратегия не зарегистрирована"));
    }
}
