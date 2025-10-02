package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.interfaces.OnCreateGroup;
import ru.practicum.shareit.interfaces.OnPatchGroup;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookingRequestDto {

    @NotNull(groups = OnPatchGroup.class)
    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    @NotNull(groups = {OnCreateGroup.class, OnPatchGroup.class}, message = "Вещь для аренды должна быть указана")
    private Long itemId;

    private Long booker;

    private Status status = Status.WAITING;
}
