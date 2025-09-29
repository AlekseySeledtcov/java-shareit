package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Transactional
    @Override
    public BookingResponseDto postBooking(Long userId, BookingRequestDto bookingRequestDto) {
        log.debug("postBooking. Добавление бронирования {}", bookingRequestDto);
        bookingRequestDto.setBooker(userId);

        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
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
    public Collection<BookingResponseDto> getBookingByState(String state, Long userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        State stateEnum;
        try {
            stateEnum = State.valueOf(state);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Аргумент не соответствует перечислению");
        }

        List<Booking> bookings = switch (stateEnum) {
            case ALL -> bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
            case CURRENT ->
                    bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
            case PAST -> bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
            case FUTURE ->
                    bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
            case WAITING -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING.name());
            case REJECTED ->
                    bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED.name());
        };

        return bookings.stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public Collection<BookingResponseDto> getBookingByStateCurrentOwner(String state, Long userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        State stateEnum;
        try {
            stateEnum = State.valueOf(state);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Аргумент не соответствует перечислению");
        }

        Collection<Booking> bookings = switch (stateEnum) {
            case ALL -> bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
            case CURRENT -> bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId,
                    LocalDateTime.now(),
                    LocalDateTime.now());
            case PAST ->
                    bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
            case FUTURE ->
                    bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
            case WAITING -> bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
            case REJECTED -> bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
        };

        return bookings.stream()
                .map(bookingMapper::toDto)
                .toList();
    }
}
