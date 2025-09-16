package ru.practicum.shareit.interfaces;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    Item toEntity(ItemRequestDto itemRequestDto);

    ItemResponseDto toDto(Item item);
}
