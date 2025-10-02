package ru.practicum.shareit.interfaces;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

@Named("UserMapperUtil")
@Component
@RequiredArgsConstructor
public class UserMapperUtil {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Named("UserIdToUser")
    public User mapUserIdToUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
    }

    @Named("mapUser")
    public UserResponseDto mapUser(User user) {
        return userMapper.toDto(user);
    }
}
