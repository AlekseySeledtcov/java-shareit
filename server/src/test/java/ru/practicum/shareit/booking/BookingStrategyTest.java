package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingStrategyTest {

    @Autowired
    private final UserService userService;

    @Autowired
    private final ItemService itemService;

    @Autowired
    private final BookingService bookingService;

    private Long ownerId;
    private Long bookerId;
    private Long itemId;

    private BookingResponseDto currentBooking;
    private BookingResponseDto pastBooking;
    private BookingResponseDto futureBooking;

    @BeforeEach
    void setup() {
        // Создаем пользователей
        ownerId = createUser("Иван Иваныч", "Ivan@mail.com").getId();
        bookerId = createUser("Петр Петрович", "Petr@mail.com").getId();

        // Создаем вещь для бронирования
        itemId = createItem("Дрель", "Чтоб сверлить", true, ownerId).getId();

        // Создаем бронирования
        currentBooking = createBooking(LocalDateTime.now(), LocalDateTime.now().plusMinutes(2));
        pastBooking = createBooking(LocalDateTime.now().minusMinutes(20), LocalDateTime.now().minusMinutes(10));
        futureBooking = createBooking(LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(3));
        BookingResponseDto rejectedBooking = createRejectedBooking(LocalDateTime.now().minusMinutes(50), LocalDateTime.now().minusMinutes(45));
    }


    private UserResponseDto createUser(String name, String email) {
        return userService.postUser(new UserRequestDto(name, email));
    }

    private ItemResponseDto createItem(String name, String description, boolean available, Long ownerId) {
        return itemService.postItem(new ItemRequestDto(name, description, available, null, null), ownerId);
    }

    private BookingResponseDto createBooking(LocalDateTime start, LocalDateTime end) {
        BookingRequestDto request = new BookingRequestDto(start, end, itemId, bookerId);
        return bookingService.postBooking(bookerId, request);
    }

    private BookingResponseDto createRejectedBooking(LocalDateTime start, LocalDateTime end) {
        BookingRequestDto request = new BookingRequestDto(start, end, itemId, bookerId);
        request.setStatus(Status.REJECTED);
        return bookingService.postBooking(bookerId, request);
    }

    @Test
    void testGetCurrentBookingsCurrentUser() {
        List<BookingResponseDto> result = bookingService.getBookingByStateCurrentUser("current", bookerId);

        assertThat(result, notNullValue());
        assertThat(result.size(), is(1));
        assertThat(result.getFirst(), is(currentBooking));
    }

    @Test
    void testGetPastBookingsCurrentUser() {
        List<BookingResponseDto> result = bookingService.getBookingByStateCurrentUser("past", bookerId);

        assertThat(result, notNullValue());
        assertThat(result.size(), is(2));
        assertThat(result.getFirst(), is(pastBooking));
    }

    @Test
    void testGetFutureBookingsCurrentUser() {
        List<BookingResponseDto> result = bookingService.getBookingByStateCurrentUser("future", bookerId);

        assertThat(result, notNullValue());
        assertThat(result.size(), is(1));
        assertThat(result.getFirst(), is(futureBooking));
    }

    @Test
    void testGetWaitingBookingsCurrentUser() {
        List<BookingResponseDto> result = bookingService.getBookingByStateCurrentUser("waiting", bookerId);

        assertThat(result, notNullValue());
        assertThat(result.size(), is(3));
    }

    @Test
    void testGetRejectedBookingsCurrentUser() {
        List<BookingResponseDto> result = bookingService.getBookingByStateCurrentUser("rejected", bookerId);

        assertThat(result, notNullValue());
        assertThat(result.size(), is(1));
        assertThat(result.getFirst().getStatus(), is(Status.REJECTED));
    }

    @Test
    void testGetAllBookingsCurrentUser() {
        List<BookingResponseDto> result = bookingService.getBookingByStateCurrentUser("all", bookerId);

        assertThat(result, notNullValue());
        assertThat(result.size(), is(4));
    }

    @Test
    void testGetCurrentBookingsCurrentOwner() {
        List<BookingResponseDto> result = bookingService.getBookingByStateCurrentOwner("current", ownerId);

        assertThat(result, notNullValue());
        assertThat(result.size(), is(1));
        assertThat(result.get(0), is(currentBooking));
    }

    @Test
    void testGetPastBookingsCurrentOwner() {
        List<BookingResponseDto> result = bookingService.getBookingByStateCurrentOwner("past", ownerId);

        assertThat(result, notNullValue());
        assertThat(result.size(), is(2));
        assertThat(result.get(0), is(pastBooking));
    }

    @Test
    void testGetFutureBookingsCurrentOwner() {
        List<BookingResponseDto> result = bookingService.getBookingByStateCurrentOwner("future", ownerId);

        assertThat(result, notNullValue());
        assertThat(result.size(), is(1));
        assertThat(result.get(0), is(futureBooking));
    }

    @Test
    void testGetWaitingBookingsCurrentOwner() {
        List<BookingResponseDto> result = bookingService.getBookingByStateCurrentOwner("waiting", ownerId);

        assertThat(result, notNullValue());
        assertThat(result.size(), is(3));
    }

    @Test
    void testGetRejectedBookingsCurrentOwner() {
        List<BookingResponseDto> result = bookingService.getBookingByStateCurrentOwner("rejected", ownerId);

        assertThat(result, notNullValue());
        assertThat(result.size(), is(1));
        assertThat(result.get(0).getStatus(), is(Status.REJECTED));
    }

    @Test
    void testGetAllBookingsCurrentOwner() {
        List<BookingResponseDto> result = bookingService.getBookingByStateCurrentOwner("all", ownerId);

        assertThat(result, notNullValue());
        assertThat(result.size(), is(4));
    }
}




