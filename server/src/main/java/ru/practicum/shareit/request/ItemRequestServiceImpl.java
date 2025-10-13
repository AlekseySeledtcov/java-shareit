package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.interfaces.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    public ItemRequestDto postItemRequest(Long requestorId, ItemRequestDto itemRequestDto) {
        userService.getById(requestorId);

        ItemRequest itemRequest = itemRequestRepository.save(itemRequestMapper.toEntity(itemRequestDto));

        return itemRequestMapper.toDto(itemRequest);
    }

    @Override
    public List<ItemRequestWithItemsDto> getItemRequest(Long requestorId) {
        userService.getUser(requestorId);

        return null;
    }

    @Override
    public List<ItemRequestDto> getItemRequestAll() {
        return null;
    }

    @Override
    public ItemRequestWithItemsDto getRequestById(Long requestId) {
        return null;
    }
}
