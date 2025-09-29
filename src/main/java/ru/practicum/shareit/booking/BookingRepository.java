package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {


    @Query("SELECT b FROM Booking AS b " +
            "JOIN FETCH b.item " +
            "WHERE b.id = :bookingId AND b.item.owner.id = :userId")
    Optional<Booking> findByIdAndOwnerId(Long bookingId, Long userId);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE b.id = :bookingId AND (b.item.owner.id = :userId OR b.booker.id = :userId)")
    Optional<Booking> findByIdAndOwnerIdOrBookerId(Long bookingId, Long userId);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long userId,
                                                                                 LocalDateTime start,
                                                                                 LocalDateTime end);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime start);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, String state);

    Collection<Booking> findAllByItemOwnerIdOrderByStartDesc(Long userId);

    Collection<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long userId,
                                                                                          LocalDateTime start,
                                                                                          LocalDateTime end);

    Collection<Booking> findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime end);

    Collection<Booking> findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime start);

    Collection<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long userId, Status status);

    Booking findFirstByItemIdAndItemOwnerIdAndStatusAndEndIsBeforeOrderByEndDesc(Long itemId,Long userId, Status status, LocalDateTime start);


    Booking findFirstByItemIdAndItemOwnerIdAndStatusAndStartIsAfterOrderByStartAsc(Long itemId, Long userId, Status status, LocalDateTime start);

    Optional<Booking> findFirstByItemIdAndBookerIdAndStatusAndStartIsBefore(Long itemId,
                                                                            Long bookerId,
                                                                            Status status,
                                                                            LocalDateTime start);
}

