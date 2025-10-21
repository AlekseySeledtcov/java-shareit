package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface ItemService {
    ItemResponseDto postItem(ItemRequestDto requestDto, Long ownerId);

    ItemResponseDto patchItem(Long itemId, Long userId, ItemRequestDto itemRequestDto);

    ItemResponseWithBookingDateDto getItemById(Long itemId, Long userId);

    List<ItemResponseDto> getItemByText(String text);

    List<ItemResponseWithBookingDateDto> getItems(Long userId);

    CommentDto postComment(CommentDto commentDto, Long itemId, Long userId, LocalDateTime timeNow);

    List<ItemResponseWithOwnerIdDto> findItemByRequestIdWithOwnerId(Long requestId);

    Item getById(Long id);
}
