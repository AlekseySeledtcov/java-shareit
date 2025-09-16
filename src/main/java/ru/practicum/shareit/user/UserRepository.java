package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User postUser(User user);

    User getUser(Long userId);

    void deleteUser(Long userId);

    List<User> getUsers();

    boolean hasEmail(String email);

    boolean containsUser(Long userId);
}
