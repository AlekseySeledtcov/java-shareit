package ru.practicum.shareit.interfaces;

import org.mapstruct.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseWithBookingDateDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        uses = {UserMapperUtil.class})
public interface ItemMapper {

    @Mapping(target = "owner", source = "owner", qualifiedByName = {"UserMapperUtil", "UserIdToUser"})
    Item toEntity(ItemRequestDto itemRequestDto);

    @Mapping(target = "request", ignore = true)
    ItemResponseDto toDto(Item item);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "owner", ignore = true)
    Item updateField(ItemRequestDto itemRequestDto, @MappingTarget Item oldItem);

    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "lastBooking", source = "bookingsIsBefore.start")
    @Mapping(target = "nextBooking", source = "bookingsIsAfter.start")
    ItemResponseWithBookingDateDto toWithBookingDateDto(Item item,
                                                        Booking bookingsIsBefore,
                                                        Booking bookingsIsAfter);

    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "lastBooking", source = "bookingsIsBefore.start")
    @Mapping(target = "nextBooking", source = "bookingsIsAfter.start")
    ItemResponseWithBookingDateDto toWithBookingDateAndCommentsDto(Item item,
                                                                   Booking bookingsIsBefore,
                                                                   Booking bookingsIsAfter,
                                                                   List<CommentDto> comments);
}
