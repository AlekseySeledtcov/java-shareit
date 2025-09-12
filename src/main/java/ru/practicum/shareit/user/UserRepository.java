package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> postUser(User user);

    Optional<User> getUser(Long userId);

    void deleteUser(Long userId);

    List<User> getUsers();
}
