package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemResponseDto postItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @Valid @RequestBody ItemRequestDto itemRequestDto) {
        itemRequestDto.setOwner(userId);
        log.debug("postItem. Добаление вещи");
        return itemService.postItem(itemRequestDto);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto patchItem(@PathVariable(name = "itemId") Long itemId,
                                     @RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestBody ItemRequestDto itemRequestDto) {
        log.debug("patchItem. Обновление вещи");
        return itemService.patchItem(itemId, userId, itemRequestDto);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItemById(@PathVariable("itemId") Long itemId,
                                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("patchItem. Получение вещи по itemId={}", itemId);
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemResponseDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("getItems. Получение списка вещей пользователя userId={}", userId);
        return itemService.getItems(userId);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> getItemByText(@RequestParam(defaultValue = "") String text) {
        log.debug("getItemByText. Получение списка вещей по запросу text={}", text);
        return itemService.getItemByText(text);
    }

}
