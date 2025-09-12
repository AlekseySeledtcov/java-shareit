package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.AlreadyExistsException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.InternalServerException;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private long id = 1L;

    @Override
    public UserResponseDto postUser(UserRequestDto userRequestDto) {
        log.debug("postUser. Добавление пользователя");

        if (containsUserByEmail(userRequestDto.getEmail())) {
            throw new AlreadyExistsException("Такой объект уже существует");
        }

        User user = UserMapper.mapToUser(userRequestDto);
        user.setId(getUserId());

        user = userRepository.postUser(user).orElseThrow(() -> {
            throw new InternalServerException("Ошибка записи");
        });
        return UserMapper.mapToUserResponseDto(user);
    }

    @Override
    public UserResponseDto patchUser(UserRequestDto userRequestDto, Long userId) {
        log.debug("patchUser. Обновление полей пользователя");

        User oldUser = userRepository.getUser(userId).orElseThrow(() -> {
            throw new EntityNotFoundException("Пользователь не найден");
        });

        User newUser = UserMapper.updateUserFields(userRequestDto, oldUser);

        userRepository.deleteUser(oldUser.getId());

        if (containsUserByEmail(newUser.getEmail())) {
            userRepository.postUser(oldUser);
            throw new AlreadyExistsException("Пользователь с таким email уже существует");
        }

        newUser = userRepository.postUser(newUser).orElseThrow(() -> {
            throw new InternalServerException("Ошибка записи");
        });

        return UserMapper.mapToUserResponseDto(newUser);
    }

    @Override
    public UserResponseDto getUser(Long userId) {
        log.debug("getUser. Получение пользователя по userId={}", userId);
        User user = userRepository.getUser(userId).orElseThrow(() -> {
            return new EntityNotFoundException("Пользователь не найден");
        });
        return UserMapper.mapToUserResponseDto(user);
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
                .map(UserMapper::mapToUserResponseDto)
                .toList();
    }

    private boolean containsUserByEmail(String email) {
        log.debug("containsUserByEmail {} ", email);
        return userRepository.getUsers().stream()
                .anyMatch(u -> u.getEmail().equals(email));
    }

    private long getUserId() {
        return id++;
    }
}