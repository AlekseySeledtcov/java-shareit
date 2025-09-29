package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDto {

    private Long id;

    @NotBlank(message = "Текст коментария должен быть указан")
    private String text;

    private String authorName;

    private LocalDateTime created;
}
