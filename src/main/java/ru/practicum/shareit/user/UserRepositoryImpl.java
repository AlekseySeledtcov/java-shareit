package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final HashMap<Long, User> inMemoryUserRepository = new HashMap<>();

    @Override
    public Optional<User> postUser(User user) {
        inMemoryUserRepository.put(user.getId(), user);
        return Optional.ofNullable(inMemoryUserRepository.get(user.getId()));
    }

    @Override
    public Optional<User> getUser(Long userId) {
        return Optional.ofNullable(inMemoryUserRepository.get(userId));
    }

    @Override
    public void deleteUser(Long userId) {
        inMemoryUserRepository.remove(userId);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(inMemoryUserRepository.values());
    }
}