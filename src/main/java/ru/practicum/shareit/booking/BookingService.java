package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.Collection;

public interface BookingService {

    BookingResponseDto postBooking(Long userId, BookingRequestDto bookingRequestDto);

    BookingResponseDto patchBooking(Long bookingId, Boolean approved, Long userId);

    BookingResponseDto getBookingDataById(Long bookingId, Long userId);

    Collection<BookingResponseDto> getBookingByState(String state, Long userId);

    Collection<BookingResponseDto> getBookingByStateCurrentOwner(String state, Long userId);

}
