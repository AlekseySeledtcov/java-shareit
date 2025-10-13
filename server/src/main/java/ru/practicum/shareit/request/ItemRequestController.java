package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.RequestHeaders;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    // добавить новый запрос вещи.
    @PostMapping
    public ItemRequestDto postItemRequest(@RequestHeader(RequestHeaders.USER_ID) Long requestorId,
                                          @Validated @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.postItemRequest(requestorId, itemRequestDto);
    }

    //  получить список своих запросов вместе с данными об ответах на них.
    //  Запросы должны возвращаться отсортированными от более новых к более старым.
    @GetMapping
    public List<ItemRequestWithItemsDto> getItemRequest(@RequestHeader(RequestHeaders.USER_ID) Long requestorId) {
        return itemRequestService.getItemRequest(requestorId);
    }

    // получить список запросов, созданных другими пользователями.
    @GetMapping("/all")
    public List<ItemRequestDto> getItemRequestAll() {
        return itemRequestService.getItemRequestAll();
    }

    // получить данные об одном конкретном запросе вместе с данными об ответах
    @GetMapping("/{requestId}")
    public ItemRequestWithItemsDto getRequestById(@PathVariable(name ="requestId") Long requestId) {
        return itemRequestService.getRequestById(requestId);
    }

}
