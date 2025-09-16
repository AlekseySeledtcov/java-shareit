package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.interfaces.OnCreateGroup;
import ru.practicum.shareit.interfaces.OnPatchGroup;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserResponseDto postUser(@Validated(OnCreateGroup.class) @RequestBody UserRequestDto userRequestDto) {
        log.debug("postUser. Добавление пользователя {}", userRequestDto);
        return userService.postUser(userRequestDto);
    }

    @PatchMapping("/{userId}")
    public UserResponseDto patchUser(@Validated(OnPatchGroup.class) @RequestBody UserRequestDto userRequestDto,
                                     @PathVariable("userId") Long userId) {
        log.debug("PatchUser. Обновление полей полльзователя с userId = {}", userId);
        return userService.patchUser(userRequestDto, userId);
    }

    @GetMapping("/{userId}")
    public UserResponseDto getUser(@PathVariable("userId") Long userId) {
        log.debug("getUser. Получение пользователя по userId {}", userId);
        return userService.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.debug("deleteUser. Удаление пользователя с userId {}", userId);
        userService.deleteUser(userId);
    }

    @GetMapping
    public List<UserResponseDto> getUsers() {
        log.debug("getUsers. Получение списка пользователей");
        return userService.getUsers();
    }
}
