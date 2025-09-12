package ru.practicum.shareit.booking.model;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {
    private long id;
    @NotNull(message = "Время должно быть указано")
    private LocalDateTime start;
    @Future(message = "Время окончания аренды не может быть в прошлом")
    private LocalDateTime end;
    @NotNull(message = "Вещь бронирования должны быть указана")
    private long item;
    @NotNull(message = "Пользователь должен быть указан")
    private long booker;
    @NotNull(message = "Статус должен быть указан")
    private Status status;
}
