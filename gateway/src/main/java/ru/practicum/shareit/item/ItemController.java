package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.RequestHeaders;
import ru.practicum.shareit.interfaces.OnCreateGroup;
import ru.practicum.shareit.interfaces.OnPatchGroup;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import java.util.Collections;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> postItem(@RequestHeader(RequestHeaders.USER_ID) @Positive Long userId,
                                           @Validated(OnCreateGroup.class) @RequestBody ItemRequestDto itemRequestDto) {
        log.debug("postItem. Добавление вещи пользователя с id {}", userId);
        return itemClient.postItem(itemRequestDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patchItem(@PathVariable("itemId") @Positive Long itemId,
                                            @RequestHeader(RequestHeaders.USER_ID) @Positive Long userId,
                                            @Validated(OnPatchGroup.class) @RequestBody ItemRequestDto itemRequestDto) {
        log.debug("patchItem. Обновление вещи с id {} , пользователя с id {}", itemId, userId);
        return itemClient.patchItem(itemId, userId, itemRequestDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@Valid @PathVariable("itemId") @Positive Long itemId,
                                              @RequestHeader(RequestHeaders.USER_ID) @Positive Long userId) {
        log.debug("getItemById. Получение вещи по itemId={}", itemId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(RequestHeaders.USER_ID) @Positive Long userId) {
        log.debug("getItems. Получение списка вещей пользователя userId={}", userId);
        return itemClient.getItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemByText(@RequestHeader(RequestHeaders.USER_ID) @Positive Long userId,
                                                @RequestParam(defaultValue = "") String text) {
        log.debug("getItemByText. Получение списка вещей по запросу text={}", text);

        if (text == null || text.isBlank()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        return itemClient.getItemByText(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@Validated @RequestBody CommentDto commentDto,
                                  @PathVariable(name = "itemId") @Positive Long itemId,
                                  @RequestHeader(RequestHeaders.USER_ID) @Positive Long userId) {
        log.debug("postComment. Добавление отзыва");
        return itemClient.postComment(userId, itemId, commentDto);
    }

}
