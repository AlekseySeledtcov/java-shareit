package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.interfaces.ItemRequestMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemResponseWithOwnerIdDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRequestMapper itemRequestMapper;
    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;
    @Mock

    ItemRequestServiceImpl itemRequestService;

    private Long requestorId;
    private User user;
    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;
    private ItemRequestDto itemRequestDto1;
    private ItemRequestDto itemRequestDto2;

    @BeforeEach
    void setup() {
        itemRequestService = new ItemRequestServiceImpl(
                itemRequestRepository,
                itemRequestMapper,
                userService,
                itemService
        );


        user = new User(1L, "User name");
        requestorId = user.getId();

        itemRequest1 = new ItemRequest(1L, "Description1", user);
        itemRequest1.setCreated(LocalDateTime.now());
        itemRequest2 = new ItemRequest(2L, "Description2", user);
        itemRequest2.setCreated(LocalDateTime.now().plusMinutes(10));

        itemRequestDto1 = new ItemRequestDto("Description1", user.getId());
        itemRequestDto2 = new ItemRequestDto("Description2", user.getId());
    }

    @Test
    void postItemRequest() {
        when(userService.getById(requestorId)).thenReturn(user);
        when(itemRequestMapper.toEntity(itemRequestDto1)).thenReturn(itemRequest1);
        when(itemRequestRepository.save(itemRequest1)).thenReturn(itemRequest1);
        when(itemRequestMapper.toDto(itemRequest1)).thenReturn(itemRequestDto1);

        ItemRequestDto actual = itemRequestService.postItemRequest(requestorId, itemRequestDto1);

        assertEquals(itemRequestDto1, actual);
    }

    @Test
    void getItemRequest() {
        ItemResponseWithOwnerIdDto itemResponseWithOwnerIdDto1 = new ItemResponseWithOwnerIdDto(1L, "Item name1", 4L);
        ItemResponseWithOwnerIdDto itemResponseWithOwnerIdDto2 = new ItemResponseWithOwnerIdDto(2L, "Item name2", 4L);
        ItemResponseWithOwnerIdDto itemResponseWithOwnerIdDto3 = new ItemResponseWithOwnerIdDto(3L, "Item name3", 4L);

        List<ItemRequest> itemRequests = List.of(itemRequest1, itemRequest2);
        List<ItemResponseWithOwnerIdDto> itemsFromRequest1 = List.of(itemResponseWithOwnerIdDto1, itemResponseWithOwnerIdDto2);
        List<ItemResponseWithOwnerIdDto> itemsFromRequest2 = List.of(itemResponseWithOwnerIdDto3);

        ItemRequestWithItemsDto itemRequestWithItemsDto1 = new ItemRequestWithItemsDto(
                itemRequest1.getId(),
                itemRequest1.getDescription(),
                requestorId,
                itemRequest1.getCreated(),
                itemsFromRequest1
        );

        ItemRequestWithItemsDto itemRequestWithItemsDto2 = new ItemRequestWithItemsDto(
                itemRequest2.getId(),
                itemRequest2.getDescription(),
                requestorId,
                itemRequest2.getCreated(),
                itemsFromRequest2
        );

        when(userService.getById(requestorId)).thenReturn(user);
        when(itemRequestRepository.findAllByRequestorId(requestorId)).thenReturn(Optional.of(itemRequests));
        when(itemService.findItemByRequestIdWithOwnerId(itemRequest1.getId())).thenReturn(itemsFromRequest1);
        when(itemService.findItemByRequestIdWithOwnerId(itemRequest2.getId())).thenReturn(itemsFromRequest2);
        when(itemRequestMapper.toWithItemsDto(itemRequest1, itemsFromRequest1)).thenReturn(itemRequestWithItemsDto1);
        when(itemRequestMapper.toWithItemsDto(itemRequest2, itemsFromRequest2)).thenReturn(itemRequestWithItemsDto2);

        List<ItemRequestWithItemsDto> result = itemRequestService.getItemRequest(requestorId);

        verify(itemRequestMapper, times(2)).toWithItemsDto(any(ItemRequest.class), any(List.class));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.getFirst().getCreated().isAfter(result.getLast().getCreated()));
    }

    @Test
    void getItemRequestIfRequestorNotExist() {
        when(userService.getById(requestorId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getItemRequest(requestorId));
        verify(itemRequestMapper, never()).toWithItemsDto(any(), any());
    }

    @Test
    void getItemRequestIfItemNotExist() {
        when(userService.getById(requestorId)).thenReturn(user);
        when(itemRequestRepository.findAllByRequestorId(requestorId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getItemRequest(requestorId));
        verify(itemRequestMapper, never()).toWithItemsDto(any(), any());
    }

    @Test
    void getItemRequestAll() {
        itemRequest2.setRequestor(new User(2L, "Another user"));
        List<ItemRequest> itemRequests = List.of(itemRequest2);

        when(userService.getById(requestorId)).thenReturn(user);
        when(itemRequestRepository.findAllByIdNotOrderByCreatedDesc(requestorId)).thenReturn(Optional.of(itemRequests));
        when(itemRequestMapper.toListDto(itemRequests)).thenReturn(List.of(itemRequestDto2));

        List<ItemRequestDto> actual = itemRequestService.getItemRequestAll(requestorId);

        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals(itemRequestDto2.getId(), actual.getFirst().getId());
        assertEquals(itemRequestDto2.getDescription(), actual.getFirst().getDescription());
        assertEquals(itemRequestDto2.getRequestor(), actual.getFirst().getRequestor());

        verify(itemRequestMapper, times(1)).toListDto(itemRequests);
    }

    @Test
    void getRequestByIdIfRequestorNotExist() {
        when(userService.getById(requestorId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getRequestById(requestorId, itemRequestDto1.getId()));
        verify(itemRequestMapper, never()).toWithItemsDto(any(), any());
    }

    @Test
    void getRequestByIdIfItemNotExist() {
        when(userService.getById(requestorId)).thenReturn(user);

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getRequestById(requestorId, 15L));
        verify(itemRequestMapper, never()).toWithItemsDto(any(), any());
    }
}