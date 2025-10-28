package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.common.RequestHeaders;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.interfaces.OnCreateGroup;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> postBooking(
            @RequestHeader(RequestHeaders.USER_ID) @Positive Long userId,
            @Validated(OnCreateGroup.class) @RequestBody BookingRequestDto bookingRequestDto) {

        log.debug("postBooking. Запрос на добавление брони");
        return bookingClient.postBooking(userId, bookingRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> patchBooking(
            @PathVariable("bookingId") @Positive Long bookingId,
            @RequestParam("approved") @NotNull Boolean approved,
            @RequestHeader(RequestHeaders.USER_ID) @Positive Long userId) {

        log.debug("patchBooking. Запрос на обновление брони по id {}", bookingId);
        return bookingClient.patchBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingDataById(
            @PathVariable("bookingId") @Positive Long bookingId,
            @RequestHeader(RequestHeaders.USER_ID) @Positive Long userId) {

        log.debug("getBookingDataById. Запрос на получение брони по id {}", bookingId);
        return bookingClient.getBookingDataById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingByState(
            @RequestParam(name = "state", defaultValue = "ALL") String stringState,
            @RequestHeader(RequestHeaders.USER_ID) @Positive Long userId) {

        BookingState state = BookingState.from(stringState)
                .orElseThrow(() -> new BadRequestException("Некорректный статус бронирования"));

        log.debug("getBookingByState. Запрос на получение брони пользователя id {} по параметру {}", userId, state);
        return bookingClient.getBookingByStateCurrentUser(userId, state);
    }


    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingByStateCurrentOwner(
            @RequestParam(name = "state", defaultValue = "ALL") String stringState,
            @RequestHeader(RequestHeaders.USER_ID) @Positive Long userId) {

        BookingState state = BookingState.from(stringState)
                .orElseThrow(() -> new BadRequestException("Некорректный статус бронирования"));

        log.debug("getBookingByStateCurrentOwner. Запрос на получение списка бронирования для всех вещей текущего пользователя");
        return bookingClient.getBookingByStateCurrentOwner(userId, state);
    }
}