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
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDateTime;

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
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private BookingServiceImpl bookingService;

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
        ownerUserResponseDto = userService.postUser(ownerUserRequestDto);
        bookerUserResponseDto = userService.postUser(bookerUserRequestDto);

        // Создаем тестовые вещи
        itemRequestDto = new ItemRequestDto("Молоток", "Чтоб стучать", true, null, null);
        ItemRequestDto itemRequestDto1 = new ItemRequestDto("Дрель", "Чтоб сверлить", true, null, null);
        ItemRequestDto itemRequestDto2 = new ItemRequestDto("Болгарка", "Чтоб пилить", true, null, null);
        ItemRequestDto itemRequestDto3 = new ItemRequestDto("Шуруповерт", "Чтоб крутить", true, null, null);

        itemResponseDto1 = itemService.postItem(itemRequestDto1, ownerUserResponseDto.getId());
        itemResponseDto2 = itemService.postItem(itemRequestDto2, ownerUserResponseDto.getId());
        itemResponseDto3 = itemService.postItem(itemRequestDto3, ownerUserResponseDto.getId());
    }

    @Test
    void testPostItemSuccess() {
        // Дано
        Long ownerId = ownerUserResponseDto.getId();

        // Когда
        ItemResponseDto response = itemService.postItem(itemRequestDto, ownerId);

        // Тогда
        assertNotNull(response);
        assertNotNull(response.getId());
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
        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setStart(LocalDateTime.now().minusMinutes(10));
        bookingRequestDto.setEnd(LocalDateTime.now().minusMinutes(5));
        bookingRequestDto.setItemId(itemId);
        bookingRequestDto.setBooker(userId);

        //Бронируем вещь чтоб пройти проверки
        BookingResponseDto bookingResponseDto = bookingService.postBooking(userId, bookingRequestDto);

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
}