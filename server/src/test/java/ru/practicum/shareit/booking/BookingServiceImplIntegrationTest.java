package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
class BookingServiceImplIntegrationTest {

    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private BookingService bookingService;

    private BookingResponseDto bookingResponseDtoCurrent;
    private BookingResponseDto bookingResponseDtoPast;
    private BookingResponseDto bookingResponseDtoFuture;
    private BookingResponseDto bookingResponseDtoRejected;

    private Long ownerId;

    @BeforeEach
    void setup() {
        // Создаем пользователей
        UserRequestDto ownerRequestDto = new UserRequestDto("Иван Иваныч", "Ivan@mail.com");
        UserResponseDto ownerResponseDto = userService.postUser(ownerRequestDto);
        ownerId = ownerResponseDto.getId();

        UserRequestDto requestorRequestDto = new UserRequestDto("Петр Петрович", "Petr@mail.com");
        UserResponseDto requestorResponseDto = userService.postUser(requestorRequestDto);
        Long bookerId = requestorResponseDto.getId();

        // Создаем вещь для бронирования
        ItemRequestDto itemRequestDto = new ItemRequestDto("Дрель", "Чтоб сверлить", true, null, null);
        ItemResponseDto itemResponseDto = itemService.postItem(itemRequestDto, ownerId);
        Long itemId = itemResponseDto.getId();

        // Создаем бронирования
        BookingRequestDto bookingRequestDtoCurrent = new BookingRequestDto(LocalDateTime.now(), LocalDateTime.now().plusMinutes(2), itemId, bookerId);
        bookingResponseDtoCurrent = bookingService.postBooking(bookerId, bookingRequestDtoCurrent);

        BookingRequestDto bookingRequestDtoPast = new BookingRequestDto(LocalDateTime.now().minusMinutes(20), LocalDateTime.now().minusMinutes(10), itemId, bookerId);
        bookingResponseDtoPast = bookingService.postBooking(bookerId, bookingRequestDtoPast);

        BookingRequestDto bookingRequestDtoFuture = new BookingRequestDto(LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(3), itemId, bookerId);
        bookingResponseDtoFuture = bookingService.postBooking(bookerId, bookingRequestDtoFuture);

        BookingRequestDto bookingRequestDtoRejected = new BookingRequestDto(LocalDateTime.now().minusMinutes(50), LocalDateTime.now().minusMinutes(45), itemId, bookerId);
        bookingRequestDtoRejected.setStatus(Status.REJECTED);
        bookingResponseDtoRejected = bookingService.postBooking(bookerId, bookingRequestDtoRejected);
    }

    @Test
    void getBookingByStateCurrentOwnerStateCurrent() {
        // Когда
        List<BookingResponseDto> expected = bookingService.getBookingByStateCurrentOwner("current", ownerId);

        // Тогда
        assertThat(expected, notNullValue());
        assertThat(expected.size(), equalTo(1));
        assertThat(expected.getFirst().getId(), equalTo(bookingResponseDtoCurrent.getId()));
        assertThat(expected.getFirst().getStart(), equalTo(bookingResponseDtoCurrent.getStart()));
        assertThat(expected.getFirst().getEnd(), equalTo(bookingResponseDtoCurrent.getEnd()));
        assertThat(expected.getFirst().getItem(), equalTo(bookingResponseDtoCurrent.getItem()));
        assertThat(expected.getFirst().getBooker(), equalTo(bookingResponseDtoCurrent.getBooker()));
        assertThat(expected.getFirst().getStatus(), equalTo(bookingResponseDtoCurrent.getStatus()));
    }

    @Test
    void getBookingByStateCurrentOwnerStatePast() {
        // Когда
        List<BookingResponseDto> expected = bookingService.getBookingByStateCurrentOwner("paSt", ownerId);

        // Тогда
        assertThat(expected, notNullValue());
        assertThat(expected.size(), equalTo(2));
        assertThat(expected.getFirst().getId(), equalTo(bookingResponseDtoPast.getId()));
        assertThat(expected.getFirst().getStart(), equalTo(bookingResponseDtoPast.getStart()));
        assertThat(expected.getFirst().getEnd(), equalTo(bookingResponseDtoPast.getEnd()));
        assertThat(expected.getFirst().getItem(), equalTo(bookingResponseDtoPast.getItem()));
        assertThat(expected.getFirst().getBooker(), equalTo(bookingResponseDtoPast.getBooker()));
        assertThat(expected.getFirst().getStatus(), equalTo(bookingResponseDtoPast.getStatus()));
    }

    @Test
    void getBookingByStateCurrentOwnerStateFuture() {
        // Когда
        List<BookingResponseDto> expected = bookingService.getBookingByStateCurrentOwner("future", ownerId);

        // Тогда
        assertThat(expected, notNullValue());
        assertThat(expected.size(), equalTo(1));
        assertThat(expected.getFirst().getId(), equalTo(bookingResponseDtoFuture.getId()));
        assertThat(expected.getFirst().getStart(), equalTo(bookingResponseDtoFuture.getStart()));
        assertThat(expected.getFirst().getEnd(), equalTo(bookingResponseDtoFuture.getEnd()));
        assertThat(expected.getFirst().getItem(), equalTo(bookingResponseDtoFuture.getItem()));
        assertThat(expected.getFirst().getBooker(), equalTo(bookingResponseDtoFuture.getBooker()));
        assertThat(expected.getFirst().getStatus(), equalTo(bookingResponseDtoFuture.getStatus()));
    }

    @Test
    void getBookingByStateCurrentOwnerStateWaiting() {
        // Когда
        List<BookingResponseDto> expected = bookingService.getBookingByStateCurrentOwner("WAITING", ownerId);

        // Тогда
        assertThat(expected, notNullValue());
        assertThat(expected.size(), equalTo(3));
    }

    @Test
    void getBookingByStateCurrentOwnerStateRejected() {
        // Когда
        List<BookingResponseDto> expected = bookingService.getBookingByStateCurrentOwner("Rejected", ownerId);

        // Тогда
        assertThat(expected, notNullValue());
        assertThat(expected.size(), equalTo(1));
        assertThat(expected.getFirst().getStatus(), equalTo(Status.REJECTED));
    }

    @Test
    void getBookingByStateCurrentOwnerStateAll() {
        // Когда
        List<BookingResponseDto> expected = bookingService.getBookingByStateCurrentOwner("aLL", ownerId);

        // Тогда
        assertThat(expected, notNullValue());
        assertThat(expected.size(), equalTo(4));
    }

    @Test
    void getBookingByStateCurrentOwnerWithIncorrectStatus() {
        try {
            bookingService.getBookingByStateCurrentOwner("MyStatus", ownerId);
        } catch (IllegalArgumentException exception) {
            assertThat(exception, notNullValue());
            assertThat(exception.getMessage(), is(any(String.class)));
        }
    }
}