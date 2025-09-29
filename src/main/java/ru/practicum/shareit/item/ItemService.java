package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseWithBookingDateDto;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    ItemResponseDto postItem(ItemRequestDto requestDto);

    ItemResponseDto patchItem(Long itemId, Long userId, ItemRequestDto itemRequestDto);

    ItemResponseWithBookingDateDto getItemById(Long itemId, Long userId);

    List<ItemResponseDto> getItemByText(String text);

    Collection<ItemResponseWithBookingDateDto> getItems(Long userId);

    CommentDto postComment(CommentDto commentDto, Long itemId, Long userId);
}
