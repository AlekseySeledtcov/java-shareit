package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class BookingRequestDto {

    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
    private Long booker;
    private Status status = Status.WAITING;

    public BookingRequestDto(LocalDateTime start, LocalDateTime end, Long itemId, Long booker) {
        this.start = start;
        this.end = end;
        this.itemId = itemId;
        this.booker = booker;
    }
}
