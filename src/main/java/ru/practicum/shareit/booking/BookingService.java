package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.util.List;

public interface BookingService {

    BookingResponseDto postBooking(Long userId, BookingRequestDto bookingRequestDto);

    BookingResponseDto patchBooking(Long bookingId, Boolean approved, Long userId);

    BookingResponseDto getBookingDataById(Long bookingId, Long userId);

    List<BookingResponseDto> getBookingByState(String state, Long userId);

    List<BookingResponseDto> getBookingByStateCurrentOwner(String state, Long userId);

    Booking findLastBooking(Long itemId, Long userId, Status status);

    Booking findNextBooking(Long itemId, Long userId, Status status);

    void checkingThatTheUserHasRentedTheItem(Long itemId, Long userId, Status status);


}
