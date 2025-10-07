package ru.practicum.shareit.booking.BookingStrategy;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.exceptions.BadRequestException;

import java.util.ArrayList;
import java.util.List;

@Component
public class BookingStrategyFactory {
    private final List<BookingStrategy> strategies = new ArrayList<>();

    public BookingStrategyFactory(BookingRepository bookingRepository) {
        strategies.add(new AllBookingsStrategy(bookingRepository));
        strategies.add(new CurrentBookingStrategy(bookingRepository));
        strategies.add(new FutureBookingStrategy(bookingRepository));
        strategies.add(new PastBookingStrategy(bookingRepository));
        strategies.add(new RejectedBookingStrategy(bookingRepository));
        strategies.add(new WaitingBookingStrategy(bookingRepository));
    }

    public BookingStrategy getBookingStrategy(BookingState bookingState) {
        return strategies.stream()
                .filter(bookingStrategy -> bookingStrategy.getState() == bookingState)
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Для состояния " + bookingState + " стратегия не зарегистрирована"));
    }
}
