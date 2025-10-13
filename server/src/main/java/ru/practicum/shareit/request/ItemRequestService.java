package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto postItemRequest(Long requestorId, ItemRequestDto itemRequestDto);

    List<ItemRequestWithItemsDto> getItemRequest(Long requestorId);

    List<ItemRequestDto> getItemRequestAll();

    ItemRequestWithItemsDto getRequestById(Long requestId);
}
