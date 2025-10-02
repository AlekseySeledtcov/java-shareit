package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.constants.RequestHeaders;
import ru.practicum.shareit.interfaces.OnCreateGroup;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto postBooking(
            @RequestHeader(RequestHeaders.USER_ID) Long userId,
            @Validated(OnCreateGroup.class) @RequestBody BookingRequestDto bookingRequestDto) {

        log.debug("postBooking. Запрос на добавление брони");
        return bookingService.postBooking(userId, bookingRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto patchBooking(
            @PathVariable("bookingId") Long bookingId,
            @RequestParam("approved") Boolean approved,
            @RequestHeader(RequestHeaders.USER_ID) Long userId) {

        log.debug("patchBooking. Запрос на обновление брони по id {}", bookingId);
        return bookingService.patchBooking(bookingId, approved, userId);
    }


    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingDataById(
            @PathVariable("bookingId") Long bookingId,
            @RequestHeader(RequestHeaders.USER_ID) Long userId) {

        log.debug("getBookingDataById. Запрос на получение брони по id {}", bookingId);
        return bookingService.getBookingDataById(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponseDto> getBookingByState(
            @RequestParam(name = "state", defaultValue = "ALL") String state,
            @RequestHeader(RequestHeaders.USER_ID) Long userId) {

        log.debug("getBookingByState. Запрос на получение брони пользователя id {} по параметру {}", userId, state);
        return bookingService.getBookingByState(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingByStateCurrentOwner(
            @RequestHeader(name = "state", defaultValue = "ALL") String state,
            @RequestHeader(RequestHeaders.USER_ID) Long userId) {

        log.debug("getBookingByStateCurrentOwner. Запрос на получение списка бронирования для всех вещей текущего пользователя");
        return bookingService.getBookingByStateCurrentOwner(state, userId);
    }
}
