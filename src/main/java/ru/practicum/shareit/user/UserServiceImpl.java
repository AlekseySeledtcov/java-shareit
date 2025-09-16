package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.AlreadyExistsException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.interfaces.UserMapper;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private long id = 1L;

    @Override
    public UserResponseDto postUser(UserRequestDto userRequestDto) {
        log.debug("postUser. Добавление пользователя");

        if (userRepository.hasEmail(userRequestDto.getEmail())) {
            throw new AlreadyExistsException("Такой объект уже существует");
        }

        User user = userMapper.toEntity(userRequestDto);
        user.setId(getUserId());
        log.debug("postUser. Добавление пользователя {}", user);
        return userMapper.toDto(userRepository.postUser(user));
    }

    @Override
    public UserResponseDto patchUser(UserRequestDto userRequestDto, Long userId) {
        log.debug("patchUser. Обновление полей пользователя {}", userRequestDto);

        if (!userRepository.containsUser(userId)) {
            throw new EntityNotFoundException("Пользователь не найден");
        }

        User oldUser = userRepository.getUser(userId);

        userRepository.deleteUser(userId);

        User patchedUser = userRequestDto.updateUserFields(oldUser);

        if (userRepository.hasEmail(patchedUser.getEmail())) {
            userRepository.postUser(oldUser);
            throw new AlreadyExistsException("Пользователь с таким email уже существует");
        }

        return userMapper.toDto(userRepository.postUser(patchedUser));
    }

    @Override
    public UserResponseDto getUser(Long userId) {
        log.debug("getUser. Получение пользователя по userId={}", userId);

        if (!userRepository.containsUser(userId)) {
            throw new EntityNotFoundException("Пользователь не найден");
        }

        User user = userRepository.getUser(userId);

        return userMapper.toDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        log.debug("deleteUser. Удаление пользователя по userId");
        userRepository.deleteUser(userId);
    }

    @Override
    public List<UserResponseDto> getUsers() {
        log.debug("getUsers. Получение списка пользователей");
        return userRepository.getUsers().stream()
                .map(userMapper::toDto)
                .toList();
    }

    private long getUserId() {
        return id++;
    }
}