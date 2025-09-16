package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> inMemoryUserRepository = new HashMap<>();
    private final Set<String> emailList = new HashSet<>();

    @Override
    public User postUser(User user) {
        inMemoryUserRepository.put(user.getId(), user);
        emailList.add(user.getEmail());
        return getUser(user.getId());
    }

    @Override
    public User getUser(Long userId) {
        return inMemoryUserRepository.get(userId);
    }

    @Override
    public void deleteUser(Long userId) {
        String email = inMemoryUserRepository.get(userId).getEmail();
        inMemoryUserRepository.remove(userId);
        emailList.remove(email);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(inMemoryUserRepository.values());
    }

    @Override
    public boolean hasEmail(String email) {
        return emailList.contains(email);
    }

    @Override
    public boolean containsUser(Long userId) {
        return inMemoryUserRepository.containsKey(userId);
    }
}