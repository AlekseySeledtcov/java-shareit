package ru.practicum.shareit.item;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;

@NoArgsConstructor
public class ItemMapper {
    public static ItemResponseDto mapToItemResponseDto(Item item) {
        ItemResponseDto itemResponseDto = new ItemResponseDto();

        itemResponseDto.setId(item.getId());
        itemResponseDto.setName(item.getName());
        itemResponseDto.setDescription(item.getDescription());
        itemResponseDto.setAvailable(item.isAvailable());

        return itemResponseDto;
    }

    public static Item mapToItem(ItemRequestDto itemRequestDto) {
        Item item = new Item();

        item.setName(itemRequestDto.getName());
        item.setDescription(itemRequestDto.getDescription());
        item.setAvailable(itemRequestDto.getAvailable());
        item.setOwner(itemRequestDto.getOwner());

        return item;
    }

    public static Item updateItemField(ItemRequestDto itemRequestDto, Item item) {
        if (itemRequestDto.hasName()) {
            item.setName(itemRequestDto.getName());
        }
        if (itemRequestDto.hasDescription()) {
            item.setDescription(itemRequestDto.getDescription());
        }

        if (itemRequestDto.hasAvailable()) {
            item.setAvailable(itemRequestDto.getAvailable());
        }

        return item;
    }
}
