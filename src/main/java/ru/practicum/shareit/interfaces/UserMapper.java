package ru.practicum.shareit.interfaces;

import org.mapstruct.*;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface UserMapper {
    User toEntity(UserRequestDto model);

    UserResponseDto toDto(User entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    User updateField(UserRequestDto userRequestDto, @MappingTarget User oldUser);
}
