package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseWithBookingDateDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private BookingServiceImpl bookingService;
    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemRepository itemRepository;


    private UserResponseDto ownerUserResponseDto;
    private UserResponseDto bookerUserResponseDto;
    private ItemRequestDto itemRequestDto;
    private ItemResponseDto itemResponseDto1;
    private ItemResponseDto itemResponseDto2;
    private ItemResponseDto itemResponseDto3;
    private Comment comment;

    @BeforeEach
    void setUp() {
        // Создаем пользователей
        UserRequestDto ownerUserRequestDto = new UserRequestDto("Иван Иваныч", "test@mail1.com");
        UserRequestDto bookerUserRequestDto = new UserRequestDto("Петр Петрович", "test@mail2.com");
        ownerUserResponseDto = createUser("Иван Иваныч", "test@mail1.com");
        bookerUserResponseDto = createUser("Петр Петрович", "test@mail2.com");
        Long bookerUserId = bookerUserResponseDto.getId();

        // Создаем запрос на вещь
        ru.practicum.shareit.request.dto.ItemRequestDto requestDto = createRequest("чтоб жужало", bookerUserId);
        Long requestId = requestDto.getId();

        // Создаем тестовые вещи
        itemRequestDto = new ItemRequestDto("Молоток", "Чтоб стучать", true, null, requestId);
        ItemRequestDto itemRequestDto1 = new ItemRequestDto("Дрель", "Чтоб сверлить", true, null, requestId);
        ItemRequestDto itemRequestDto2 = new ItemRequestDto("Болгарка", "Чтоб пилить", true, null, requestId);
        ItemRequestDto itemRequestDto3 = new ItemRequestDto("Шуруповерт", "Чтоб крутить", true, null, requestId);

        itemResponseDto1 = itemService.postItem(itemRequestDto1, ownerUserResponseDto.getId());
        itemResponseDto2 = itemService.postItem(itemRequestDto2, ownerUserResponseDto.getId());
        itemResponseDto3 = itemService.postItem(itemRequestDto3, ownerUserResponseDto.getId());
    }

    private UserResponseDto createUser(String name, String mail) {
        return userService.postUser(new UserRequestDto(name, mail));
    }

    private ru.practicum.shareit.request.dto.ItemRequestDto createRequest(String description, Long requestor) {
        return itemRequestService.postItemRequest(requestor, new ru.practicum.shareit.request.dto.ItemRequestDto(description, requestor));
    }

    private BookingResponseDto createBooking(LocalDateTime start, LocalDateTime end, Long itemId, Long booker) {
        return bookingService.postBooking(booker, new BookingRequestDto(start, end, itemId, booker));
    }

    private ItemResponseDto createItem(ItemRequestDto itemRequestDto, Long ownerId) {
        return itemService.postItem(itemRequestDto, ownerId);
    }

    private CommentDto createComment(CommentDto comment, Long itemId, Long userId, LocalDateTime timeNow) {
        return itemService.postComment(comment, itemId, userId, timeNow);
    }

    @Test
    void testPostItemSuccess() {
        // Дано
        Long ownerId = ownerUserResponseDto.getId();

        // Когда
        ItemResponseDto response = createItem(itemRequestDto, ownerId);

        // Тогда
        assertNotNull(response);
        assertEquals(itemRequestDto.getName(), response.getName());
        assertEquals(itemRequestDto.getDescription(), response.getDescription());
        assertEquals(itemRequestDto.getAvailable(), response.isAvailable());

        // Проверяем сохранение в БД
        Item savedItem = itemRepository.findById(response.getId())
                .orElseThrow(() -> new EntityNotFoundException("Вещь не найдена в базе"));

        assertEquals(itemRequestDto.getName(), savedItem.getName());
        assertEquals(itemRequestDto.getDescription(), savedItem.getDescription());
        assertEquals(itemRequestDto.getAvailable(), savedItem.getAvailable());
    }

    @Test
    void testPostItemInvalidOwnerId() {
        // Дано несуществующий ID владельца
        Long nonExistingOwnerId = -1L;

        // Когда и Тогда
        assertThrows(EntityNotFoundException.class, () -> {
            itemService.postItem(itemRequestDto, nonExistingOwnerId);
        });
    }

    @Test
    void getItemById() {
        // Дано
        Long itemId = itemResponseDto3.getId();
        Long userId = bookerUserResponseDto.getId();

        // Когда
        ItemResponseWithBookingDateDto expected = itemService.getItemById(itemId, userId);

        // Тогда
        assertNotNull(expected);
        assertEquals(expected.getName(), itemResponseDto3.getName());
        assertEquals(expected.getDescription(), itemResponseDto3.getDescription());
        assertNotNull(expected.getComments());
    }

    @Test
    void getItemByIdInvalidOwnerId() {
        // Дано несуществующий ID владельца
        Long nonExistingOwnerId = -1L;
        Long itemId = itemResponseDto1.getId();

        // Когда и Тогда
        assertThrows(EntityNotFoundException.class, () -> {
            itemService.getItemById(itemId, nonExistingOwnerId);
        });
    }

    @Test
    void postComment() {
        // Дано
        Long itemId = itemResponseDto2.getId();
        Long userId = bookerUserResponseDto.getId();

        // Создаем бронь
        BookingResponseDto bookingResponseDto = createBooking(
                LocalDateTime.now().minusMinutes(10),
                LocalDateTime.now().minusMinutes(5),
                itemId,
                userId);

        // Обновляем бронь, меняем статус на APPROVE
        bookingService.patchBooking(bookingResponseDto.getId(), true, ownerUserResponseDto.getId());

        // Создаем запросс на коментарий
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Пилит так, что искры летят");
        commentDto.setAuthorName("Петр Петрович");
        commentDto.setCreated(LocalDateTime.now());
        // Когда
        CommentDto expected = itemService.postComment(commentDto, itemId, userId, LocalDateTime.now());
        assertNotNull(expected);
        assertNotNull(expected.getId());
        assertEquals(expected.getText(), commentDto.getText());
        assertEquals(expected.getAuthorName(), commentDto.getAuthorName());
        assertNotNull(expected.getCreated());
    }

    @Test
    void patchItem() {
        // Дано
        Long itemId = itemResponseDto1.getId();
        Long ownerId = ownerUserResponseDto.getId();
        Long requestorId = bookerUserResponseDto.getId();
        // Создаем запрос для добавление в поле вещи
        ru.practicum.shareit.request.dto.ItemRequestDto patchRequest = createRequest("Нужен перфоратор", requestorId);
        Long requestId = patchRequest.getId();
        // Создаем POJO которыйм будем обновлять
        ItemRequestDto patchItem = new ItemRequestDto("Перфоратор", "Чтоб бурить", true, ownerId, requestId);

        // Когда
        ItemResponseDto patchedItem = itemService.patchItem(itemId, ownerId, patchItem);

        //Тогда
        assertNotNull(patchedItem);
        assertEquals(patchedItem.getId(), itemId);
        assertEquals(patchedItem.getName(), patchItem.getName());
        assertEquals(patchedItem.getDescription(), patchItem.getDescription());
        assertEquals(patchedItem.isAvailable(), patchItem.getAvailable());
        assertNotEquals(patchedItem.getRequest(), itemResponseDto1.getRequest());

    }

    @Test
    void getItems() {
        // Дано
        Long itemId1 = itemResponseDto1.getId();
        Long itemId2 = itemResponseDto2.getId();
        Long itemId3 = itemResponseDto3.getId();

        Long ownerId = ownerUserResponseDto.getId();
        Long bookerId = bookerUserResponseDto.getId();
        String authorName = bookerUserResponseDto.getName();

            // Создаем бронь
        BookingResponseDto bookingResponse1 = createBooking(LocalDateTime.now().minusMinutes(15), LocalDateTime.now().minusMinutes(14), itemId1, bookerId);
        BookingResponseDto bookingResponse2 = createBooking(LocalDateTime.now().minusMinutes(13), LocalDateTime.now().minusMinutes(12), itemId2, bookerId);
        BookingResponseDto bookingResponse3 = createBooking(LocalDateTime.now().minusMinutes(11), LocalDateTime.now().minusMinutes(10), itemId3, bookerId);
            // Обновляем бронь, меняем статус на APPROVE
        bookingService.patchBooking(bookingResponse1.getId(), true, ownerId);
        bookingService.patchBooking(bookingResponse2.getId(), true, ownerId);
        bookingService.patchBooking(bookingResponse3.getId(), true, ownerId);
            // Создаем коммент
        createComment(new CommentDto("Отлично", authorName), itemId3, bookerId, LocalDateTime.now());

        // Когда
        List<ItemResponseWithBookingDateDto> result = itemService.getItems(ownerId);

        // Тогда
        assertNotNull(result);
        assertEquals(3, result.size());
    }
}