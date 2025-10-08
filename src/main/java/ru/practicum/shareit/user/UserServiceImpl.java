package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public UserResponseDto postUser(UserRequestDto userRequestDto) {
        log.debug("postUser. Добавление пользователя {} ", userRequestDto);

        if (userRepository.findByEmail(userRequestDto.getEmail()).isPresent()) {
            throw new AlreadyExistsException("Такой пользователь уже существует");
        }

        User user = userRepository.save(userMapper.toEntity(userRequestDto));
        log.debug("postUser. Добавление пользователя {} ", user);
        return userMapper.toDto(user);
    }

    @Transactional
    @Override
    public UserResponseDto patchUser(UserRequestDto userRequestDto, Long userId) {
        log.debug("patchUser. Обновление полей пользователя {}", userRequestDto);

        User newUser = getById(userId);

        userRepository.findByEmail(userRequestDto.getEmail()).ifPresent(user -> {
                    if (!user.getId().equals(userId)) {
                        throw new AlreadyExistsException("Такой email уже существует");
                    }
                }
        );

        userMapper.updateField(userRequestDto, newUser);
        userRepository.updateUser(newUser.getId(), newUser.getName(), newUser.getEmail());

        return userMapper.toDto(getById(newUser.getId()));
    }

    @Override
    public UserResponseDto getUser(Long userId) {
        log.debug("getUser. Получение пользователя по userId={}", userId);

        return userMapper.toDto(getById(userId));
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        log.debug("deleteUser. Удаление пользователя по userId");
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserResponseDto> getUsers() {
        log.debug("getUsers. Получение списка пользователей");

        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        PageRequest page = PageRequest.of(0, 32, sort);

        return userRepository.findAll(page).stream()
                .map(userMapper::toDto)
                .toList();
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
    }
}