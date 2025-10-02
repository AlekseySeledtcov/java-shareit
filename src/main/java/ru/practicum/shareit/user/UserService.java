package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    UserResponseDto postUser(UserRequestDto userRequestDto);

    UserResponseDto patchUser(UserRequestDto userRequestDto, Long userId);

    UserResponseDto getUser(Long userId);

    void deleteUser(Long userId);

    List<UserResponseDto> getUsers();

    User getById(Long id);
}
