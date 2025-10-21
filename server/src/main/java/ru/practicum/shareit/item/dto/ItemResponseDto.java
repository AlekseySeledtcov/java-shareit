package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ItemResponseDto {
    private long id;
    private String name;
    private String description;
    private boolean available;
    private long request;
}
