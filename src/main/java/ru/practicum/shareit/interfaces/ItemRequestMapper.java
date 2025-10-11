package ru.practicum.shareit.interfaces;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemRequestMapper {

    ItemRequest toEntity(ItemRequestDto itemRequestDto);

    ItemRequestDto toDto(ItemRequest itemRequest);
}
