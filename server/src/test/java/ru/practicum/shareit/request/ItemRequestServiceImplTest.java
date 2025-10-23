package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.interfaces.ItemRequestMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemResponseWithOwnerIdDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

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
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setup() {
        itemRequestService = new ItemRequestServiceImpl(
                itemRequestRepository,
                itemRequestMapper,
                userService,
                itemService
        );

        requestorId = 1L;

        user = new User(1L, "User name");

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Description");
        itemRequest.setRequestor(user);

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Description");
        itemRequestDto.setRequestor(1L);
    }

    @Test
    void postItemRequest() {
        when(userService.getById(requestorId)).thenReturn(user);
        when(itemRequestMapper.toEntity(itemRequestDto)).thenReturn(itemRequest);
        when(itemRequestRepository.save(itemRequest)).thenReturn(itemRequest);
        when(itemRequestMapper.toDto(itemRequest)).thenReturn(itemRequestDto);

        ItemRequestDto actual = itemRequestService.postItemRequest(requestorId, itemRequestDto);

        assertEquals(itemRequestDto, actual);
    }

    @Test
    void getItemRequest() {
        List<ItemRequest> itemRequests = List.of(itemRequest);

        ItemResponseWithOwnerIdDto itemResponseWithOwnerIdDto = new ItemResponseWithOwnerIdDto();
        itemResponseWithOwnerIdDto.setId(1L);
        itemResponseWithOwnerIdDto.setName("Item name");
        itemResponseWithOwnerIdDto.setOwnerId(2L);

        ItemRequestWithItemsDto itemRequestWithItemsDto = new ItemRequestWithItemsDto();
        itemRequestWithItemsDto.setId(itemRequest.getId());
        itemRequestWithItemsDto.setDescription(itemRequest.getDescription());
        itemRequestWithItemsDto.setRequestor(requestorId);
        itemRequestWithItemsDto.setCreated(itemRequest.getCreated());
        itemRequestWithItemsDto.setItems(List.of(itemResponseWithOwnerIdDto));


        when(userService.getById(requestorId)).thenReturn(user);
        when(itemRequestRepository.findAllByRequestorId(requestorId)).thenReturn(Optional.of(itemRequests));
        when(itemService.findItemByRequestIdWithOwnerId(itemRequest.getId())).thenReturn(List.of(itemResponseWithOwnerIdDto));
        when(itemRequestMapper.toWithItemsDto(any(ItemRequest.class), any(List.class))).thenReturn(itemRequestWithItemsDto);

        List<ItemRequestWithItemsDto> actual = itemRequestService.getItemRequest(requestorId);

        verify(itemRequestMapper, times(1)).toWithItemsDto(any(ItemRequest.class), any(List.class));

        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals(itemRequest.getId(), actual.getFirst().getId());
        assertEquals(itemRequest.getDescription(), actual.getFirst().getDescription());
        assertEquals(itemRequest.getRequestor().getId(), actual.getFirst().getRequestor());
        assertEquals(itemRequest.getCreated(), actual.getFirst().getCreated());
        assertNotNull(actual.getFirst().getItems());
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
        List<ItemRequest> itemRequests = List.of(itemRequest);

        when(userService.getById(requestorId)).thenReturn(user);
        when(itemRequestRepository.findAll(any(Sort.class))).thenReturn(itemRequests);
        when(itemRequestMapper.toListDto(itemRequests)).thenReturn(List.of(itemRequestDto));

        List<ItemRequestDto> actual = itemRequestService.getItemRequestAll(requestorId);

        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals(itemRequestDto.getId(), actual.get(0).getId());
        assertEquals(itemRequestDto.getDescription(), actual.get(0).getDescription());
        assertEquals(itemRequestDto.getRequestor(), actual.get(0).getRequestor());

        verify(itemRequestMapper, times(1)).toListDto(itemRequests);
    }

    @Test
    void getRequestByIdIfRequestorNotExist() {
        when(userService.getById(requestorId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getRequestById(requestorId, itemRequestDto.getId()));
        verify(itemRequestMapper, never()).toWithItemsDto(any(), any());
    }

    @Test
    void getRequestByIdIfItemNotExist() {
        when(userService.getById(requestorId)).thenReturn(user);

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getRequestById(requestorId, 15L));
        verify(itemRequestMapper, never()).toWithItemsDto(any(), any());
    }
}