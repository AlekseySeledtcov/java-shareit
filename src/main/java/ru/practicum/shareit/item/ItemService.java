package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import java.util.List;

public interface ItemService {
    ItemResponseDto postItem(ItemRequestDto requestDto);

    ItemResponseDto patchItem(Long itemId, Long userId, ItemRequestDto itemRequestDto);

    ItemResponseDto getItemById(Long itemId, Long userId);

    List<ItemResponseDto> getItems(Long userId);

    List<ItemResponseDto> getItemByText(String text);
}
