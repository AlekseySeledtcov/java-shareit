package ru.practicum.shareit.interfaces;

import org.mapstruct.*;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface ItemMapper {
    Item toEntity(ItemRequestDto itemRequestDto);

    ItemResponseDto toDto(Item item);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    Item updateField(ItemRequestDto itemRequestDto, @MappingTarget Item oldItem);
}
