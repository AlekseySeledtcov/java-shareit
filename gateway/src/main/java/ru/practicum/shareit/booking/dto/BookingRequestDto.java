package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.interfaces.OnCreateGroup;
import ru.practicum.shareit.interfaces.OnPatchGroup;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class BookingRequestDto {

    @NotNull(groups = OnPatchGroup.class)
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime start;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime end;

    @NotNull(groups = {OnCreateGroup.class, OnPatchGroup.class}, message = "Вещь для аренды должна быть указана")
    private Long itemId;

    private Long booker;

    private Status status = Status.WAITING;

}
