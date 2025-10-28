package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto postItemRequest(Long requestorId, ItemRequestDto itemRequestDto);

    List<ItemRequestWithItemsDto> getItemRequest(Long requestorId);

    List<ItemRequestDto> getItemRequestAll(Long requestorId);

    ItemRequestWithItemsDto getRequestById(Long requestorId, Long requestId);
}
