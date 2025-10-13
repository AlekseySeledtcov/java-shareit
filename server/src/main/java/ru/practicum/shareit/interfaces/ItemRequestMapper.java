package ru.practicum.shareit.interfaces;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {UserMapperUtil.class})
public interface ItemRequestMapper {

    @Mapping(target = "requestor", source = "requestor", qualifiedByName = {"UserMapperUtil", "UserIdToUser"})
    ItemRequest toEntity(ItemRequestDto itemRequestDto);

    @Mapping(target = "requestor", source = "requestor.id")
    ItemRequestDto toDto(ItemRequest itemRequest);
}
