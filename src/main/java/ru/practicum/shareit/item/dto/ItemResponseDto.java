package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemResponseDto {
    private long id;
    private String name;
    private String description;
    private boolean available;
    private long request;
}
