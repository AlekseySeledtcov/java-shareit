package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemResponseWithOwnerIdDto {
    private Long id;
    private String name;
    private Long ownerId;
}
