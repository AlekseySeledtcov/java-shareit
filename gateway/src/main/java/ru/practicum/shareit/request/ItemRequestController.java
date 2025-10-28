package ru.practicum.shareit.request;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.RequestHeaders;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    // добавить новый запрос вещи.
    @PostMapping
    public ResponseEntity<Object> postItemRequest(@RequestHeader(RequestHeaders.USER_ID) @Positive Long requestorId,
                                                  @Validated @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestClient.postItemRequest(requestorId, itemRequestDto);
    }

    //  получить список своих запросов вместе с данными об ответах на них.
    //  Запросы должны возвращаться отсортированными от более новых к более старым.
    @GetMapping
    public ResponseEntity<Object> getItemRequest(@RequestHeader(RequestHeaders.USER_ID) @Positive Long requestorId) {
        return itemRequestClient.getItemRequest(requestorId);
    }

    // получить список запросов, созданных другими пользователями.
    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequestAll(@RequestHeader(RequestHeaders.USER_ID) @Positive Long requestorId) {
        return itemRequestClient.getItemRequestAll(requestorId);
    }

    // получить данные об одном конкретном запросе вместе с данными об ответах
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable(name = "requestId") @Positive Long requestId,
                                                 @RequestHeader(RequestHeaders.USER_ID) @Positive Long requestorId) {
        return itemRequestClient.getRequestById(requestorId, requestId);
    }

}
