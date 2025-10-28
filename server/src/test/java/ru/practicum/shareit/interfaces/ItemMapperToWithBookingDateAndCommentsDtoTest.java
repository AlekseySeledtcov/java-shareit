package ru.practicum.shareit.interfaces;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseWithBookingDateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@Transactional
@ExtendWith(SpringExtension.class)
@SpringBootTest
class ItemMapperToWithBookingDateAndCommentsDtoTest {

    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ItemRepository itemRepository;

    private List<CommentDto> comments;
    private Item itemClub;
    private Booking bookingsIsBefore;
    private Booking bookingsIsAfter;


    @BeforeEach
    void setUp() {
        // Создаем пользователей
        UserResponseDto ownerResponseDto = createUser("Князь Киевский", "Vladimir@mail.com");
        Long ownerId = ownerResponseDto.getId();
        UserResponseDto user1ResponseDto = createUser("Алеша Попович", "Popovich@mail.com");
        Long userId1 = user1ResponseDto.getId();
        UserResponseDto user2ResponseDto = createUser("Илья Муромец", "Muromskiy@mail.com");
        Long userId2 = user2ResponseDto.getId();
        UserResponseDto user3ResponseDto = createUser("Добрыня Никитичь", "Dobriy@mail.com");
        Long userId3 = user3ResponseDto.getId();

        // Создаем предметы
        ItemResponseDto clubDto = itemService.postItem(new ItemRequestDto("Дубина", "Тяжелая штука", true, ownerId, null), ownerId);
        Long clubId = clubDto.getId();
        // Создаем бронирования
        BookingResponseDto clubBookingDto1 = createBooking(
                userId1,
                LocalDateTime.now().minusMinutes(50),
                LocalDateTime.now().minusMinutes(10),
                clubDto.getId()
        );
        Long clubBookingId1 = clubBookingDto1.getId();
        BookingResponseDto clubBookingDto2 = createBooking(
                userId2,
                LocalDateTime.now().minusMinutes(20),
                LocalDateTime.now(),
                clubDto.getId()
        );
        Long clubBookingId2 = clubBookingDto2.getId();
        BookingResponseDto clubBookingDto3 = createBooking(
                userId3,
                LocalDateTime.now().plusMinutes(5),
                LocalDateTime.now().plusMinutes(50),
                clubDto.getId()
        );
        Long clubBookingId3 = clubBookingDto3.getId();
        // Патчим брониирования
        bookingService.patchBooking(clubBookingId1, true, ownerId);
        bookingService.patchBooking(clubBookingId2, true, ownerId);
        bookingService.patchBooking(clubBookingId3, true, ownerId);
        // Создаем комментарии
        CommentDto clubCommentRequestDto1 = new CommentDto("Тяжелая штука, но рабочая, иногода скрипит", "Алеша Попович");
        CommentDto clubCommentRequestDto2 = new CommentDto("Удобно чесать спину", "Илья Муромец");

        CommentDto clubCommentDto1 = createComment(clubCommentRequestDto1, clubId, userId1, LocalDateTime.now().minusMinutes(20));
        CommentDto clubCommentDto2 = createComment(clubCommentRequestDto2, clubId, userId2, LocalDateTime.now().minusMinutes(10));

        List<CommentDto> comments = List.of(clubCommentDto1, clubCommentDto2);

        // Получаем сущность по которой будем получать список
        itemClub = itemRepository.findById(clubId)
                .orElseThrow(() -> new EntityNotFoundException("Предмет не найден"));

        // Получаем последнее бронирование
        bookingsIsBefore = bookingService.findLastBooking(
                clubId,
                ownerId,
                Status.APPROVED,
                LocalDateTime.now()
        );
        // Получаем следующее бронировани
        bookingsIsAfter = bookingService.findNextBooking(
                clubId,
                ownerId,
                Status.APPROVED,
                LocalDateTime.now()
        );
    }

    private UserResponseDto createUser(String name, String email) {
        return userService.postUser(new UserRequestDto(name, email));
    }

    private BookingResponseDto createBooking(Long bookerId, LocalDateTime start, LocalDateTime end, Long itemId) {
        return bookingService.postBooking(bookerId, new BookingRequestDto(start, end, itemId, bookerId));
    }

    private CommentDto createComment(CommentDto commentDto, Long itemId, Long userId, LocalDateTime timeNow) {
        return itemService.postComment(commentDto, itemId, userId, timeNow);
    }

    @Test
    void toWithBookingDateAndCommentsDtoIfAllInputIsNull() {
        ItemResponseWithBookingDateDto result = itemMapper.toWithBookingDateAndCommentsDto(
                null,
                null,
                null,
                null);

        assertNull(result);
    }

    @Test
    void toWithBookingDateAndCommentsDtoIfBookingsIsBeforeNull() {
        ItemResponseWithBookingDateDto result = itemMapper.toWithBookingDateAndCommentsDto(
                itemClub,
                null,
                bookingsIsAfter,
                comments);

        assertNotNull(result);
        assertNull(result.getLastBooking());
    }

    @Test
    void toWithBookingDateAndCommentsDtoIfBookingsIsAfterNull() {
        ItemResponseWithBookingDateDto result = itemMapper.toWithBookingDateAndCommentsDto(
                itemClub,
                bookingsIsBefore,
                null,
                comments);

        assertNotNull(result);
        assertNull(result.getNextBooking());

    }

    @Test
    void toWithBookingDateAndCommentsDtoIfCommentsNull() {
        ItemResponseWithBookingDateDto result = itemMapper.toWithBookingDateAndCommentsDto(
                itemClub,
                bookingsIsBefore,
                bookingsIsAfter,
                null);

        assertNotNull(result);
        assertNull(result.getComments());
    }
}