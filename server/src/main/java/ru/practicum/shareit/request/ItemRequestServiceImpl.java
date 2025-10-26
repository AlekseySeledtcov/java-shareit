package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Comparator;
import java.util.List;

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

        return itemRequestRepository.findAllByRequestorId(requestorId)
                .orElseThrow(() -> new EntityNotFoundException("Запрос на вещь не найден"))
                .stream()
                .map(request -> {
                    List<ItemResponseWithOwnerIdDto> items = itemService.findItemByRequestIdWithOwnerId(request.getId());
                    return itemRequestMapper.toWithItemsDto(request, items);
                })
                .sorted(Comparator.comparing(ItemRequestWithItemsDto::getCreated).reversed())
                .toList();
    }

    @Override
    public List<ItemRequestDto> getItemRequestAll(Long requestorId) {
        userService.getById(requestorId);

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByIdNotOrderByCreatedDesc(requestorId)
                .orElseThrow(() -> new EntityNotFoundException("Запросы не найдены"));

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
