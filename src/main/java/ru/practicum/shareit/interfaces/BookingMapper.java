package ru.practicum.shareit.interfaces;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {UserMapperUtil.class, ItemMapperUtil.class})
public interface BookingMapper {

    @Mapping(target = "booker", source = "booker", qualifiedByName = {"UserMapperUtil", "UserIdToUser"})
    @Mapping(target = "item", source = "itemId", qualifiedByName = {"ItemMapperUtil", "ItemIdToItem"})
    Booking toEntityBooking(BookingRequestDto dto);


    @Mapping(target = "item", source = "item", qualifiedByName = {"ItemMapperUtil", "mapItem"})
    @Mapping(target = "booker", source = "booker", qualifiedByName = {"UserMapperUtil", "mapUser"})
    @Mapping(target = "id", source = "id")
    BookingResponseDto toDto(Booking booking);
}
