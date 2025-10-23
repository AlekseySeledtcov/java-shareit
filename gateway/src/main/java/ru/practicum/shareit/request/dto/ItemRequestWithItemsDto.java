package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemResponseWithOwnerIdDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ItemRequestWithItemsDto {

    private Long id;
    private String description;
    private Long requestor;
    private LocalDateTime created;
    List<ItemResponseWithOwnerIdDto> itemResponseWithOwnerIdDtos;
}
