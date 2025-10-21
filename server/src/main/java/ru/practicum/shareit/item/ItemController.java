package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.RequestHeaders;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseWithBookingDateDto;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemResponseDto postItem(@RequestHeader(RequestHeaders.USER_ID) Long userId,
                                    @RequestBody ItemRequestDto itemRequestDto) {
        log.debug("postItem. Добавление вещи {}", itemRequestDto);
        return itemService.postItem(itemRequestDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto patchItem(@PathVariable(name = "itemId") Long itemId,
                                     @RequestHeader(RequestHeaders.USER_ID) Long userId,
                                     @RequestBody ItemRequestDto itemRequestDto) {
        log.debug("patchItem. Обновление вещи с id {} , пользователя с id {}", itemId, userId);
        return itemService.patchItem(itemId, userId, itemRequestDto);
    }

    @GetMapping("/{itemId}")
    public ItemResponseWithBookingDateDto getItemById(@PathVariable("itemId") Long itemId,
                                                      @RequestHeader(RequestHeaders.USER_ID) Long userId) {
        log.debug("getItemById. Получение вещи по itemId={}", itemId);
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemResponseWithBookingDateDto> getItems(@RequestHeader(RequestHeaders.USER_ID) Long userId) {
        log.debug("getItems. Получение списка вещей пользователя userId={}", userId);
        return itemService.getItems(userId);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> getItemByText(@RequestParam("text") String text) {
        log.debug("getItemByText. Получение списка вещей по запросу text={}", text);
        return itemService.getItemByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@RequestBody CommentDto commentDto,
                                  @PathVariable(name = "itemId") Long itemId,
                                  @RequestHeader(RequestHeaders.USER_ID) Long userId) {
        log.debug("postComment. Добавление отзыва");
        return itemService.postComment(commentDto, itemId, userId, LocalDateTime.now());
    }


}
