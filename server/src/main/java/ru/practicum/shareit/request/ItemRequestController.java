package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.RequestHeaders;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto postItemRequest(@RequestHeader(RequestHeaders.USER_ID) Long requestorId,
                                          @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.debug("postItemRequest. Добавление запроса вещи {}", itemRequestDto);

        return itemRequestService.postItemRequest(requestorId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestWithItemsDto> getItemRequest(@RequestHeader(RequestHeaders.USER_ID) Long requestorId) {
        log.debug("getItemRequest. Получить список своих запросов вместе с данными об ответах на них");
        return itemRequestService.getItemRequest(requestorId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getItemRequestAll(@RequestHeader(RequestHeaders.USER_ID) Long requestorId) {
        log.debug("getItemRequestAll. Получить список запросов, созданных другими пользователями");
        return itemRequestService.getItemRequestAll(requestorId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemsDto getRequestById(@RequestHeader(RequestHeaders.USER_ID) Long requestorId,
                                                  @PathVariable("requestId") Long requestId) {
        log.debug("getRequestById. Получить данные об одном конкретном запросе вместе с данными об ответах");

        return itemRequestService.getRequestById(requestorId, requestId);
    }

}
