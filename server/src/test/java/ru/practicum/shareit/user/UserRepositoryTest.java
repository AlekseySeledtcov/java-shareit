package ru.practicum.shareit.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setName("testUser");
        userRepository.save(user);
    }

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldFindUserByEmail() {
        // Выполнение теста
        Optional<User> foundUser = userRepository.findByEmail("test@mail.com");

        // Проверки
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test@mail.com");
        assertThat(foundUser.get().getName()).isEqualTo("testUser");
    }

    @Test
    void shouldNotFindUserByNonExistingEmail() {
        // Выполнение теста
        Optional<User> foundUser = userRepository.findByEmail("nonexisting@mail.com");

        // Проверка
        assertThat(foundUser).isNotPresent();
    }
}