package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.interfaces.ItemRequestMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemResponseWithOwnerIdDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserService userService;
    private final ItemService itemService;

    @Transactional
    @Override
    public ItemRequestDto postItemRequest(Long requestorId, ItemRequestDto itemRequestDto) {
        userService.getById(requestorId);
        itemRequestDto.setRequestor(requestorId);
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = itemRequestRepository.save(itemRequestMapper.toEntity(itemRequestDto));

        return itemRequestMapper.toDto(itemRequest);
    }

    @Override
    public List<ItemRequestWithItemsDto> getItemRequest(Long requestorId) {
        userService.getById(requestorId);

        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorId(requestorId)
                .orElseThrow(() -> new EntityNotFoundException("Запрос на вещи не найден"));
        Map<Long, List<ItemResponseWithOwnerIdDto>> items = new HashMap<>();

        requests.stream()
                .map(request -> items.put(request.getId(),
                        itemService.findItemByRequestIdWithOwnerId(request.getId())))
                .toList();

        return requests.stream()
                .map(request -> {
                    return itemRequestMapper.toWithItemsDto(request, items.get(request.getId()));
                })
                .toList();
    }

    @Override
    public List<ItemRequestDto> getItemRequestAll(Long requestorId) {
        userService.getById(requestorId);

        List<ItemRequest> itemRequests = itemRequestRepository.findAll(Sort.by(Sort.Order.desc("created")));

        return itemRequestMapper.toListDto(itemRequests);
    }

    @Override
    public ItemRequestWithItemsDto getRequestById(Long requestorId, Long requestId) {
        userService.getById(requestorId);

        //Поиск запроса на вещь по id
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Запрос вещи не найден"));

        //Получаем список вещей связанных с этим запросом
        List<ItemResponseWithOwnerIdDto> item = itemService.findItemByRequestIdWithOwnerId(requestId);

        //Возвращаем DTO с собранной информацией
        return itemRequestMapper.toWithItemsDto(itemRequest, item);
    }
}
