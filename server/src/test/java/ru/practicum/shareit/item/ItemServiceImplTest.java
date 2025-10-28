package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.interfaces.CommentMapper;
import ru.practicum.shareit.interfaces.ItemMapper;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private ItemServiceImpl itemService;

    private ItemRequestDto itemRequestDto;
    private Item item;
    private ItemResponseDto itemResponseDto;
    private Long ownerId;
    private Long itemId;
    private CommentDto commentDto;

    @BeforeEach
    void setup() {
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setName("Item name");
        itemRequestDto.setDescription("Item description");
        itemRequestDto.setAvailable(true);
        itemRequestDto.setOwner(1L);
        itemRequestDto.setRequest(1L);

        item = new Item();
        item.setId(1L);
        item.setName("Item name");
        item.setDescription("Item description");
        item.setAvailable(true);
        item.setOwner(new User(1L, "Item owner"));
        item.setRequest(new ItemRequest());

        itemResponseDto = new ItemResponseDto();
        itemResponseDto.setId(1L);
        itemResponseDto.setName("Item name");
        itemResponseDto.setDescription("Item description");
        itemResponseDto.setAvailable(true);
        itemResponseDto.setRequest(1L);

        ownerId = 1L;
        itemId = 1L;

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Comment text");
        commentDto.setAuthorName("Comment Author");
        commentDto.setCreated(LocalDateTime.now());
    }

    @Test
    void postItemIfItemNotExistInDB() {
        when(userService.getById(ownerId)).thenReturn(new User());
        when(itemMapper.toEntity(itemRequestDto)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toDto(item)).thenReturn(itemResponseDto);

        ItemResponseDto actual = itemService.postItem(itemRequestDto, ownerId);

        assertEquals(itemResponseDto, actual);
        verify(itemMapper, times(1)).toDto(any(Item.class));
    }

    @Test
    void postItemIfItemExistInDBThenThrowException() {
        when(userService.getById(ownerId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> itemService.postItem(itemRequestDto, ownerId));
        verify(itemMapper, never()).toDto(any(Item.class));
    }

    @Test
    void patchItem() {
        itemRequestDto.setName("Item new name");
        ItemResponseDto expectItem = itemResponseDto;
        expectItem.setName("Item new name");

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        doAnswer(invocation -> {
            ItemRequestDto itemRequestDto1 = invocation.getArgument(0);
            Item item = invocation.getArgument(1);
            item.setName(itemRequestDto1.getName());
            return item;
        }).when(itemMapper).updateField(itemRequestDto, item);
        when(userService.getById(ownerId)).thenReturn(new User(1L, "Item owner"));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemMapper.toDto(any(Item.class))).thenReturn(itemResponseDto);

        ItemResponseDto actualItem = itemService.patchItem(itemId, ownerId, itemRequestDto);
        assertEquals(expectItem, actualItem);
        verify(itemMapper, times(1)).toDto(any(Item.class));
    }


    @Test
    void patchItemIfTheItemIsNotInTheDatabase() {
        when(itemRepository.findById(itemId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> itemService.patchItem(itemId, ownerId, itemRequestDto));
    }

    @Test
    void patchItemIfTheItemDoesNotBelongToTheUser() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        try {
            itemService.patchItem(itemId, 2L, itemRequestDto);
        } catch (EntityNotFoundException exception) {
            assertEquals("Вещь не принадлежит данному пользователю", exception.getMessage());
        }
    }

    @Test
    void getItemById() {
        when(itemRepository.findById(ownerId)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(itemId)).thenReturn(List.of(new Comment()));
        when(commentMapper.toCommentDto(any(Comment.class))).thenReturn(commentDto);
        when(bookingService.findLastBooking(anyLong(), anyLong(), any(Status.class), any(LocalDateTime.class))).thenReturn(null);
        when(bookingService.findNextBooking(anyLong(), anyLong(), any(Status.class), any(LocalDateTime.class))).thenReturn(null);
        when(itemMapper.toWithBookingDateAndCommentsDto(item, null, null, List.of(commentDto))).thenReturn(new ItemResponseWithBookingDateDto());

        ItemResponseWithBookingDateDto result = itemService.getItemById(itemId, ownerId);
        assertNotNull(result);
    }

    @Test
    void getItemByText() {
        when(itemRepository.findAllByNameOrDescriptionContaining(anyString())).thenReturn(Optional.of(List.of(item)));
        when(itemMapper.toDto(item)).thenReturn(itemResponseDto);

        List<ItemResponseDto> result = itemService.getItemByText("text");
        assertNotNull(result);
        verify(itemMapper, times(1)).toDto(any(Item.class));
    }

    @Test
    void getItems() {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setOwner(new User(1L, "Name user 1"));

        Item item2 = new Item();
        item2.setId(2L);
        item2.setOwner(new User(2L, "Name user 2"));

        Booking booking = new Booking();
        booking.setStatus(Status.APPROVED);
        booking.setStart(LocalDateTime.now());

        when(userService.getById(ownerId)).thenReturn(new User());
        when(itemRepository.findAllByUserId(ownerId)).thenReturn(Optional.of(List.of(item1, item2)));
        when(commentRepository.findAllByItemId(item1.getId())).thenReturn(List.of(new Comment()));
        when(commentRepository.findAllByItemId(item2.getId())).thenReturn(List.of(new Comment()));
        when(commentMapper.toListCommentDto(anyList())).thenReturn(List.of(commentDto));
        when(bookingService.findLastBooking(anyLong(), anyLong(), eq(Status.APPROVED), any(LocalDateTime.class))).thenReturn(booking);
        when(bookingService.findNextBooking(anyLong(), anyLong(), eq(Status.APPROVED), any(LocalDateTime.class))).thenReturn(booking);

        List<ItemResponseWithBookingDateDto> items = itemService.getItems(ownerId);

        assertEquals(2, items.size());
    }

    @Test
    void postComment() {
        Long userId = 1L;
        User author = new User(1L, "Author name");
        Comment comment = new Comment();
        comment.setText("Text comment");
        LocalDateTime timeNow = LocalDateTime.now();

        when(userService.getById(userId)).thenReturn(author);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        doNothing().when(bookingService).checkingThatTheUserHasRentedTheItem(itemId, userId, Status.APPROVED, timeNow);
        when(commentMapper.toEntityComment(commentDto, item, author)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toCommentDto(comment)).thenReturn(commentDto);

        CommentDto actual = itemService.postComment(commentDto, itemId, userId, timeNow);

        assertEquals(commentDto, actual);
        verify(commentMapper, times(1)).toCommentDto(any(Comment.class));
    }

    @Test
    void findItemByRequestIdWithOwnerId() {
        Long requestId = 1L;

        Item item1 = new Item();
        item1.setId(1L);
        item1.setOwner(new User(1L, "user1"));

        Item item2 = new Item();
        item2.setId(2L);
        item2.setOwner(new User(2L, "user"));

        List<Item> items = List.of(item1, item2);

        ItemResponseWithOwnerIdDto dto1 = new ItemResponseWithOwnerIdDto();
        dto1.setId(1L);
        dto1.setOwnerId(1L);

        ItemResponseWithOwnerIdDto dto2 = new ItemResponseWithOwnerIdDto();
        dto2.setId(2L);
        dto2.setOwnerId(2L);

        List<ItemResponseWithOwnerIdDto> expectedDtos = List.of(dto1, dto2);

        when(itemRepository.findAllByRequestId(requestId)).thenReturn(items);
        when(itemMapper.toWithOwnerIdDto(items)).thenReturn(expectedDtos);

        List<ItemResponseWithOwnerIdDto> actual = itemService.findItemByRequestIdWithOwnerId(requestId);

        assertEquals(expectedDtos, actual);
        assertEquals(2, actual.size());

        verify(itemMapper, times(1)).toWithOwnerIdDto(items);
    }

}