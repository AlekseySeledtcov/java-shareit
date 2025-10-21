package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserService userService;
    @Autowired
    ItemRequestService itemRequestService;

    private ItemRequestDto itemRequestDto;
    private Long requestorId;
    private Long ownerId;


    @BeforeEach
    void setup() {
        // Создаем пользователей
            // Запросные POJO
        UserRequestDto ownerUserRequestDto = new UserRequestDto("Иван Иваныч", "Ivan@mail.com");
        UserRequestDto requestorUserRequestDto = new UserRequestDto("Петр Петрович", "Petr@mail.com");
            // Ответы
        UserResponseDto ownerUserResponseDto = userService.postUser(ownerUserRequestDto);
        UserResponseDto requestorUserResponseDto = userService.postUser(requestorUserRequestDto);
            // ID
        requestorId = requestorUserResponseDto.getId();
        ownerId = ownerUserResponseDto.getId();
        // Создаем запрос на вещь
        itemRequestDto = new ItemRequestDto("Чтобы жужало", requestorId);
    }


    @Test
    void postItemRequest() {
        // Когда
        ItemRequestDto expected = itemRequestService.postItemRequest(requestorId, itemRequestDto);

        // Тогда
        assertThat(expected.getRequestor(), notNullValue());
        assertThat(expected.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(expected.getRequestor(), equalTo(itemRequestDto.getRequestor()));
        assertThat(expected.getCreated(), notNullValue(LocalDateTime.class));
    }

    @Test
    void getItemRequest() {
    }

    @Test
    void getItemRequestAll() {
    }

    @Test
    void getRequestById() {
        // Дано
        Long requestId = itemRequestService.postItemRequest(requestorId, itemRequestDto).getId();
        User ownerUser = userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        // Находим сущность ItemRequest в базе
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Запрос не найден"));
        // Создаем и добавляем вещи в базу всесте с информаций о запросеж
        Item item1 = new Item("Дрель", "Чтоб сверлить", true, ownerUser);
        item1.setRequest(itemRequest);
        Item item2 = new Item("Болгарка", "Чтоб пилить", true, ownerUser);
        item2.setRequest(itemRequest);
        itemRepository.save(item1);
        itemRepository.save(item2);

        // Когда
        ItemRequestWithItemsDto expected = itemRequestService.getRequestById(requestorId, requestId);

        // Тогда
        assertThat(expected, notNullValue());
        assertThat(expected.getId(), notNullValue(Long.class));
        assertThat(expected.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(expected.getRequestor(), equalTo(requestorId));
        assertThat(expected.getCreated(), notNullValue(LocalDateTime.class));
        assertThat(expected.getItems(), notNullValue(Collection.class));
        assertThat(expected.getItems().size(), equalTo(2));
    }
}