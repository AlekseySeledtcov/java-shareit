package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemResponseWithOwnerIdDto;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ItemRequestWithItemsDto {

    private Long id;
    private String description;
    private Long requestor;
    private LocalDateTime created;
    private List<ItemResponseWithOwnerIdDto> items;
}
