package ru.practicum.shareit.interfaces;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ExtendWith(SpringExtension.class)
@SpringBootTest
class ItemMapperUpdateFieldTest {

    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;
    @Autowired
    ItemRepository itemRepository;

    private Long newUserId;
    private Item oldItem;


    @BeforeEach
    void setUp() {
        UserResponseDto oldUserDto = createUser("OldUserName", "Old@email.com");
        Long oldUserId = oldUserDto.getId();
        UserResponseDto newUserDto = createUser("NewUserName", "New@email.com");
        newUserId = newUserDto.getId();

        ItemResponseDto oldItemDto = createItem("OldName", "OldDescription", oldUserId);
        oldItem = itemRepository.findById(oldItemDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Вещь не найдена"));
    }

    private UserResponseDto createUser(String name, String email) {
        return userService.postUser(new UserRequestDto(name, email));
    }

    private ItemResponseDto createItem(String name, String description, Long ownerId) {
        return itemService.postItem(new ItemRequestDto(name, description, true, ownerId, null), ownerId);
    }


    @Test
    void updateFieldIfNameFieldIsNull() {
        ItemRequestDto requestDto = new ItemRequestDto(null, "NewDescription", false, newUserId, null);

        Item item = itemMapper.updateField(requestDto, oldItem);

        assertNotEquals(requestDto.getName(), item.getName());
        assertEquals(requestDto.getDescription(), item.getDescription());
        assertEquals(requestDto.getAvailable(), item.getAvailable());
        assertNotEquals(requestDto.getOwner(), item.getOwner().getId()); //потому что поле игноррируется
        assertNull(requestDto.getRequest());
    }

    @Test
    void updateFieldIfDescriptionFieldIsNull() {
        ItemRequestDto requestDto = new ItemRequestDto("NewItem", null, false, newUserId, null);

        Item item = itemMapper.updateField(requestDto, oldItem);

        assertEquals(requestDto.getName(), item.getName());
        assertNotEquals(requestDto.getDescription(), item.getDescription());
        assertEquals(requestDto.getAvailable(), item.getAvailable());
        assertNotEquals(requestDto.getOwner(), item.getOwner().getId()); //потому что поле игноррируется
        assertNull(requestDto.getRequest());
    }

    @Test
    void updateFieldIfAvailableFieldIsNull() {
        ItemRequestDto requestDto = new ItemRequestDto("NewItem", "NewDescription", null, newUserId, null);

        Item item = itemMapper.updateField(requestDto, oldItem);

        assertEquals(requestDto.getName(), item.getName());
        assertEquals(requestDto.getDescription(), item.getDescription());
        assertNotEquals(requestDto.getAvailable(), item.getAvailable());
        assertNotEquals(requestDto.getOwner(), item.getOwner().getId()); //потому что поле игноррируется
        assertNull(requestDto.getRequest());
    }

    @Test
    void updateFieldIfRequestFieldIsNull() {
        ItemRequestDto requestDto = new ItemRequestDto("NewItem", "NewDescription", false, newUserId, null);

        Item item = itemMapper.updateField(requestDto, oldItem);

        assertEquals(requestDto.getName(), item.getName());
        assertEquals(requestDto.getDescription(), item.getDescription());
        assertEquals(requestDto.getAvailable(), item.getAvailable());
        assertNotEquals(requestDto.getOwner(), item.getOwner().getId()); //потому что поле игноррируется
        assertNull(requestDto.getRequest());
    }

    @Test
    void updateFieldIfRequestIsNull() {
        Item item = itemMapper.updateField(null, oldItem);

        assertEquals(item, oldItem);
    }
}