package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStrategy.BookingStrategy;
import ru.practicum.shareit.booking.BookingStrategy.BookingStrategyFactory;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.interfaces.BookingMapper;
import ru.practicum.shareit.interfaces.CommentMapper;
import ru.practicum.shareit.interfaces.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    BookingRepository bookingRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    BookingMapper bookingMapper;
    @Mock
    UserServiceImpl userService;
    @Mock
    BookingStrategy bookingStrategy;
    @Mock
    BookingStrategyFactory bookingStrategyFactory;

    @InjectMocks
    BookingServiceImpl bookingService;

    private Long userId;
    private Long itemId;
    private Long bookingId;
    private BookingRequestDto bookingRequestDto;
    private BookingResponseDto bookingResponseDto;
    private Booking booking;
    private User user;
    private Item item;

    @BeforeEach
    void setup() {
        userId = 1L;
        itemId = 1L;
        bookingId = 1L;

        bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(itemId);
        bookingRequestDto.setStart(LocalDateTime.now());
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(1));

        bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setId(itemId);
        bookingResponseDto.setStart(LocalDateTime.now());
        bookingResponseDto.setEnd(LocalDateTime.now().plusDays(1));

        user = new User(1L, "User name");

        item = new Item();
        item.setId(itemId);
        item.setAvailable(true);
        item.setOwner(user);

        booking = new Booking();
        booking.setId(1L);
        booking.setStatus(Status.WAITING);
        booking.setItem(item);
    }

    @Test
    void postBooking() {
        when(userService.getById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingMapper.toEntityBooking(bookingRequestDto)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(bookingResponseDto);

        BookingResponseDto actual = bookingService.postBooking(userId, bookingRequestDto);

        assertEquals(bookingResponseDto, actual);
        verify(bookingMapper, times(1)).toDto(booking);
    }

    @Test
    void postBookingIfUserNotExist() {
        when(userService.getById(userId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> bookingService.postBooking(userId, bookingRequestDto));
        verify(bookingMapper, never()).toDto(booking);
    }

    @Test
    void testPostBookingItemNotFound() {
        when(userService.getById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.postBooking(userId, bookingRequestDto));
        verify(bookingMapper, never()).toDto(booking);
    }

    @Test
    void testPostBookingItemNotAvailable() {
        item.setAvailable(false);
        when(userService.getById(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(BadRequestException.class, () -> bookingService.postBooking(userId, bookingRequestDto));
        verify(bookingMapper, never()).toDto(booking);
    }

    @Test
    void patchBooking() {
//        Long bookingId = 1L;
        Boolean approved = true;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(bookingResponseDto);

        BookingResponseDto actual = bookingService.patchBooking(bookingId, approved, userId);

        assertEquals(bookingResponseDto, actual);
        assertEquals(Status.APPROVED, booking.getStatus());
        verify(bookingMapper, times(1)).toDto(booking);
    }

    @Test
    void patchBookingIfTheItemBelongsToAnotherUser() {
//        Long bookingId = 1L;
        Boolean approved = true;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(ForbiddenException.class, () -> bookingService.patchBooking(bookingId, approved, 2L));
        verify(bookingMapper, never()).toDto(booking);
    }

    @Test
    void patchBookingIfTheStatusIsNotAppropriate() {
//        Long bookingId = 1L;
        Boolean approved = true;
        booking.setStatus(Status.CANCELED);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(BadRequestException.class, () -> bookingService.patchBooking(bookingId, approved, userId));
        verify(bookingMapper, never()).toDto(booking);

    }

    @Test
    void getBookingDataById() {
        when(bookingRepository.findByIdAndOwnerIdOrBookerId(bookingId, userId)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(bookingResponseDto);

        BookingResponseDto actual = bookingService.getBookingDataById(bookingId, userId);

        assertEquals(bookingResponseDto, actual);
        verify(bookingMapper, times(1)).toDto(booking);
    }

    @Test
    void getBookingByStateCurrentUser() {
        String state = "past";
        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setBooker(new User(1L, "User 1"));

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setBooker(new User(2L, "User 2"));

        BookingResponseDto bookingResponseDto1 = new BookingResponseDto();
        bookingResponseDto1.setId(1L);

        BookingResponseDto bookingResponseDto2 = new BookingResponseDto();
        bookingResponseDto2.setId(2L);

        List<Booking> bookings = List.of(booking1, booking2);
        List<BookingResponseDto> expectedDtos = List.of(bookingResponseDto1, bookingResponseDto2);

        when(userService.getById(userId)).thenReturn(user);
        when(bookingStrategyFactory.getBookingStrategy(BookingState.parse(state)))
                .thenReturn(bookingStrategy);
        when(bookingStrategy.findBookingsByBookerId(userId)).thenReturn(bookings);
        when(bookingMapper.toDto(booking1)).thenReturn(bookingResponseDto1);
        when(bookingMapper.toDto(booking2)).thenReturn(bookingResponseDto2);

        List<BookingResponseDto> actual = bookingService.getBookingByStateCurrentUser(state, userId);

        assertEquals(expectedDtos, actual);
        verify(bookingMapper, times(2)).toDto(any(Booking.class));
    }

    @Test
    void findLastBooking() {
        Status status = Status.APPROVED;
        LocalDateTime timeNow = LocalDateTime.now();

        Booking expectedBooking = new Booking();
        expectedBooking.setId(1L);
        expectedBooking.setItem(item);
        expectedBooking.setBooker(user);
        expectedBooking.setStatus(status);
        expectedBooking.setEnd(timeNow.minusDays(1));

        when(bookingRepository.findFirstByItemIdAndItemOwnerIdAndStatusAndEndIsBeforeOrderByEndDesc(
                itemId,
                userId,
                status,
                timeNow))
                .thenReturn(expectedBooking);

        Booking actual = bookingService.findLastBooking(itemId, userId, status, timeNow);

        assertNotNull(actual);
        assertEquals(expectedBooking.getId(), actual.getId());
        assertEquals(expectedBooking.getItem(), actual.getItem());
        assertEquals(expectedBooking.getBooker(), actual.getBooker());
        assertEquals(expectedBooking.getStatus(), actual.getStatus());
        assertEquals(expectedBooking.getEnd(), actual.getEnd());
    }

    @Test
    void findNextBooking() {
        Status status = Status.APPROVED;
        LocalDateTime timeNow = LocalDateTime.now();

        Booking expectedBooking = new Booking();
        expectedBooking.setId(1L);
        expectedBooking.setItem(item);
        expectedBooking.setBooker(user);
        expectedBooking.setStatus(status);
        expectedBooking.setEnd(timeNow.minusDays(1));

        when(bookingRepository.findFirstByItemIdAndItemOwnerIdAndStatusAndStartIsAfterOrderByStartAsc(itemId,
                userId,
                Status.APPROVED,
                timeNow)).thenReturn(expectedBooking);

        Booking actual = bookingService.findNextBooking(itemId, userId, status, timeNow);
        assertNotNull(actual);
        assertEquals(expectedBooking.getId(), actual.getId());
        assertEquals(expectedBooking.getItem(), actual.getItem());
        assertEquals(expectedBooking.getBooker(), actual.getBooker());
        assertEquals(expectedBooking.getStatus(), actual.getStatus());
        assertEquals(expectedBooking.getEnd(), actual.getEnd());
    }

    @Test
    void checkingThatTheUserHasRentedTheItem() {
        Status status = Status.APPROVED;
        LocalDateTime timeNow = LocalDateTime.of(2026, 10,18, 14, 17);

        when(bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndStartIsBefore(
                itemId,
                userId,
                status,
                timeNow))
                .thenReturn(Optional.of(booking));

        bookingService.checkingThatTheUserHasRentedTheItem(itemId, userId, status, timeNow);

        verify(bookingRepository, times(1)).findFirstByItemIdAndBookerIdAndStatusAndStartIsBefore(
                itemId,
                userId,
                status,
                timeNow);
    }
}