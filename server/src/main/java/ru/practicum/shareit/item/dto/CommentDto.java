package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@Getter
@Setter
public class CommentDto {
    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;

    public CommentDto(String text, String authorName) {
        this.text = text;
        this.authorName = authorName;
    }
}
