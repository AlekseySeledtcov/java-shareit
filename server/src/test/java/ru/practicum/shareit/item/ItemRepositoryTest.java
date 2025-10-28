package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Transactional
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void addItems() {
        User user1 = new User(1L, "Owner 1");
        user1.setEmail("User1@mail.ru");
        User user2 = new User(2L, "Owner 1");
        user2.setEmail("User2@mail.ru");
        User user3 = new User(1L, "Owner 1");
        user3.setEmail("User3@mail.ru");

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        itemRepository.save(new Item(1, "Item name 1", "description 1", true, user1));
        itemRepository.save(new Item(2, "Item name 2", "description 2", true, user2));
        itemRepository.save(new Item(3, "Item name 3", "description 3", true, user3));
    }

    @Test
    void findAllByUserId() {
        Optional<Collection<Item>> actualItems = itemRepository.findAllByUserId(1L);

        assertTrue(actualItems.isPresent());
        assertEquals(2, actualItems.get().size());
    }
}