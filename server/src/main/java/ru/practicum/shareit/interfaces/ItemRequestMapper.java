package ru.practicum.shareit.interfaces;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.item.dto.ItemResponseWithOwnerIdDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {UserMapperUtil.class})
public interface ItemRequestMapper {

    @Mapping(target = "requestor", source = "requestor", qualifiedByName = {"UserMapperUtil", "UserIdToUser"})
    ItemRequest toEntity(ItemRequestDto itemRequestDto);

    @Mapping(target = "requestor", source = "requestor.id")
    ItemRequestDto toDto(ItemRequest itemRequest);

    @Mapping(target = "requestor", source = "itemRequest.requestor.id")
    @Mapping(target = "items", source = "items")
    ItemRequestWithItemsDto toWithItemsDto(ItemRequest itemRequest,
                                           List<ItemResponseWithOwnerIdDto> items);

    @Mapping(target = "requestor", source = "requestor.id")
    List<ItemRequestDto> toListDto(List<ItemRequest> itemRequests);
}
