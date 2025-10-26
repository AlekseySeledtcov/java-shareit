package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime created;
    private List<ItemResponseWithOwnerIdDto> itemResponseWithOwnerIdDtos;
}
