package ru.practicum.shareit.interfaces;

import org.mapstruct.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        uses = {UserMapperUtil.class, ItemRequestMapperUtil.class})
public interface ItemMapper {

    //    @Mapping(target = "request", ignore = true)
    @Mapping(target = "owner", source = "owner", qualifiedByName = {"UserMapperUtil", "UserIdToUser"})
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "request", source = "request", qualifiedByName = {"ItemRequestMapperUtil", "ItemRequestIdToItemRequest"})
    Item toEntity(ItemRequestDto itemRequestDto);


    @Mapping(target = "request", source = "request.id")
    ItemResponseDto toDto(Item item);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "request", source = "request", qualifiedByName = {"ItemRequestMapperUtil", "ItemRequestIdToItemRequest"})
//    @Mapping(target = "request", ignore = true)
    Item updateField(ItemRequestDto itemRequestDto, @MappingTarget Item oldItem);

    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "lastBooking", source = "bookingsIsBefore.start")
    @Mapping(target = "nextBooking", source = "bookingsIsAfter.start")
    ItemResponseWithBookingDateDto toWithBookingDateAndCommentsDto(Item item,
                                                                   Booking bookingsIsBefore,
                                                                   Booking bookingsIsAfter,
                                                                   List<CommentDto> comments);

    @Mapping(target = "ownerId", source = "owner.id")
    List<ItemResponseWithOwnerIdDto> toWithOwnerIdDto (List<Item> items);

}
