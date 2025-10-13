package ru.practicum.shareit.user;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.interfaces.OnCreateGroup;
import ru.practicum.shareit.interfaces.OnPatchGroup;
import ru.practicum.shareit.user.dto.UserRequestDto;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> postUser(@Validated(OnCreateGroup.class) @RequestBody UserRequestDto userRequestDto) {
        log.debug("postUser. Добавление пользователя {}", userRequestDto);
        return userClient.postUser(userRequestDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> patchUser(@Validated(OnPatchGroup.class) @RequestBody UserRequestDto userRequestDto,
                                            @PathVariable("userId") @Positive Long userId) {
        log.debug("patchUser. Обновление полей пользователя с userId = {}", userId);
        return userClient.patchUser(userRequestDto, userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable("userId") @Positive Long userId) {
        log.debug("getUser. Получение пользователя по userId {}", userId);
        return userClient.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable @Positive Long userId) {
        log.debug("deleteUser. Удаление пользователя с userId {}", userId);
        return userClient.deleteUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.debug("getUsers. Получение списка пользователей");
        return userClient.getUsers();
    }
}
