package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStrategy.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.interfaces.BookingMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Transactional
    @Override
    public BookingResponseDto postBooking(Long userId, BookingRequestDto bookingRequestDto) {
        log.debug("postBooking. Добавление бронирования {}", bookingRequestDto);
        bookingRequestDto.setBooker(userId);

        userService.getById(userId);
        Item item = itemRepository.findById(bookingRequestDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("Вещь не найдена"));

        if (!item.getAvailable()) {
            throw new BadRequestException("Вещь не доступна для бронирования");
        }

        Booking booking = bookingRepository.save(bookingMapper.toEntityBooking(bookingRequestDto));

        return bookingMapper.toDto(booking);
    }

    @Transactional
    @Override
    public BookingResponseDto patchBooking(Long bookingId, Boolean approved, Long userId) {
        log.debug("patchBooking. Обновление брони с id {}, статус {}", bookingId, approved);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Бронь не найдена"));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Вещь принадлежит другому пользователю");
        }
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new BadRequestException("Нельзя поменять статус вещи");
        }

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return bookingMapper.toDto(booking);
    }

    @Override
    public BookingResponseDto getBookingDataById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findByIdAndOwnerIdOrBookerId(bookingId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Данные бронирования не найдены"));

        return bookingMapper.toDto(booking);
    }

    @Override
    public List<BookingResponseDto> getBookingByStateCurrentUser(String state, Long userId) {
        userService.getById(userId);

        BookingStrategy bookingStrategy = getBookingStrategy(state);
        return bookingStrategy.findBookingsByBookerId(userId).stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public List<BookingResponseDto> getBookingByStateCurrentOwner(String state, Long userId) {
        userService.getById(userId);

        BookingStrategy bookingStrategy = getBookingStrategy(state);

        return bookingStrategy.findBookingsByOwnerId(userId).stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public Booking findLastBooking(Long itemId, Long userId, Status status) {
        return bookingRepository.findFirstByItemIdAndItemOwnerIdAndStatusAndEndIsBeforeOrderByEndDesc(itemId,
                userId,
                Status.APPROVED,
                LocalDateTime.now());
    }

    @Override
    public Booking findNextBooking(Long itemId, Long userId, Status status) {
        return bookingRepository.findFirstByItemIdAndItemOwnerIdAndStatusAndStartIsAfterOrderByStartAsc(itemId,
                userId,
                Status.APPROVED,
                LocalDateTime.now());
    }

    @Override
    public void checkingThatTheUserHasRentedTheItem(Long itemId, Long userId, Status status) {
        bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndStartIsBefore(itemId,
                        userId,
                        Status.APPROVED,
                        LocalDateTime.now())
                .orElseThrow(() -> new BadRequestException("Неправельные параметры запроса"));
    }

    private BookingStrategy getBookingStrategy(String state) {
        return switch (state) {
            case "ALL" -> new AllBookingsStrategy(bookingRepository);
            case "CURRENT" -> new CurrentBookingStrategy(bookingRepository);
            case "PAST" -> new PastBookingStrategy(bookingRepository);
            case "FUTURE" -> new FutureBookingStrategy(bookingRepository);
            case "WAITING" -> new WaitingBookingStrategy(bookingRepository);
            case "REJECTED" -> new RejectedBookingStrategy(bookingRepository);
            default -> throw new IllegalArgumentException("Аргумент не соответствует перечислению");
        };
    }
}
