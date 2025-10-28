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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
class BookingServiceImplIntegrationTest {

    @Autowired
    private final UserService userService;
    @Autowired
    private final ItemService itemService;
    @Autowired
    private final BookingService bookingService;


    private LocalDateTime start;
    private LocalDateTime end;
    private Long ownerId;
    private Long bookerId;
    private Long itemId;

    private BookingRequestDto bookingRequestDto;

    @BeforeEach
    void setUp() {
        // Данные для тестирования
        start = LocalDateTime.of(2025, 10, 9, 11, 0, 0);
        end = LocalDateTime.of(2025, 10, 9, 11, 10, 0);

        // Создаем пользователей
        UserRequestDto ownerRequestDto = new UserRequestDto("Князь Владимир", "Vladimir@mail.com");
        UserResponseDto ownerResponseDto = createUser(ownerRequestDto);
        UserRequestDto bookerRequestDto = new UserRequestDto("Алеша Попович", "Popovich@mail.com");
        UserResponseDto bookerResponseDto = createUser(bookerRequestDto);
        ownerId = ownerResponseDto.getId();
        bookerId = bookerResponseDto.getId();
        // Создаем предметы
        ItemRequestDto clubRequestDto = new ItemRequestDto("Дубина", "Тяжелая штука", true, ownerId, null);
        ItemResponseDto clubResponseDto = createItem(clubRequestDto, ownerId);
        itemId = clubResponseDto.getId();

        // Создаем бронирование
        bookingRequestDto = new BookingRequestDto(start, end, itemId, ownerId);
    }

    private UserResponseDto createUser(UserRequestDto userRequestDto) {
        return userService.postUser(userRequestDto);
    }

    private ItemResponseDto createItem(ItemRequestDto requestDto, Long ownerId) {
        return itemService.postItem(requestDto, ownerId);
    }

    private BookingResponseDto createBooking(Long bookerId, BookingRequestDto bookingRequestDto) {
        return bookingService.postBooking(bookerId, bookingRequestDto);
    }

    @Test
    void postBookingTest() {
        // Когда
        BookingResponseDto bookingResponseDto = bookingService.postBooking(bookerId, bookingRequestDto);

        // Тогда
        assertThat(bookingResponseDto).isNotNull();
        assertThat(bookingResponseDto.getId()).isNotNull();
        assertThat(bookingResponseDto.getStart()).isEqualTo(start);
        assertThat(bookingResponseDto.getEnd()).isEqualTo(end);
        assertThat(bookingResponseDto.getItem().getId()).isEqualTo(itemId);
        assertThat(bookingResponseDto.getBooker().getId()).isEqualTo(bookerId);
        assertThat(bookingResponseDto.getStatus()).isEqualTo(Status.WAITING);
    }

    @Test
    void patchBookingTest() {
        // Создаем бронирование
        BookingResponseDto bookingResponseDto = createBooking(bookerId, bookingRequestDto);
        Long bookingId = bookingResponseDto.getId();

        // Когда
        BookingResponseDto patchedBookingResponseDto = bookingService.patchBooking(bookingId, true, ownerId);

        // Тогда
        assertThat(patchedBookingResponseDto.getStatus()).isEqualTo(Status.APPROVED);
    }

    @Test
    void getBookingDataByIdTest() {
        // Создаем бронирование
        BookingResponseDto bookingResponseDto = createBooking(bookerId, bookingRequestDto);
        Long bookingId = bookingResponseDto.getId();

        // Когда
        BookingResponseDto result = bookingService.getBookingDataById(bookingId, ownerId);

        // Тогда
        assertThat(result.getId()).isEqualTo(bookingResponseDto.getId());
        assertThat(result.getStart()).isEqualTo(bookingResponseDto.getStart());
        assertThat(result.getEnd()).isEqualTo(bookingResponseDto.getEnd());
    }
}