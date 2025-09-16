package ru.practicum.shareit.interfaces;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserRequestDto model);

    UserResponseDto toDto(User entity);
}
