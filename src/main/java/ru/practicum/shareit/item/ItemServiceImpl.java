package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.InternalServerException;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private long id = 1L;

    @Override
    public ItemResponseDto postItem(ItemRequestDto itemRequestDto) {
        log.debug("postItem. Добавление вещи ");
        Item item = ItemMapper.mapToItem(itemRequestDto);
        item.setId(getItemId());
        userService.getUser(item.getOwner());
        item = itemRepository.postItem(item).orElseThrow(() -> {
            throw new InternalServerException("Ошибка записи");
        });
        return ItemMapper.mapToItemResponseDto(item);
    }

    @Override
    public ItemResponseDto patchItem(Long itemId, Long userId, ItemRequestDto itemRequestDto) {
        Item oldItem = itemRepository.getItemById(itemId).orElseThrow(() -> {
            throw new EntityNotFoundException("Вещь не найдена");
        });

        if (oldItem.getOwner() != userId) {
            throw new EntityNotFoundException("Вещь не найдена");
        }

        Item newItem = ItemMapper.updateItemField(itemRequestDto, oldItem);
        newItem.setOwner(userId);

        return ItemMapper.mapToItemResponseDto(itemRepository.postItem(newItem).get());
    }

    @Override
    public ItemResponseDto getItemById(Long itemId, Long userId) {
        return itemRepository.getItemById(itemId)
                .filter(item -> item.getOwner() == userId)
                .map(ItemMapper::mapToItemResponseDto)
                .orElseThrow(() -> new EntityNotFoundException("Вещь не найдена"));
    }

    @Override
    public List<ItemResponseDto> getItems(Long userId) {
        return itemRepository.getItems().stream()
                .filter(item -> item.getOwner() == userId)
                .map(ItemMapper::mapToItemResponseDto)
                .toList();
    }

    @Override
    public List<ItemResponseDto> getItemByText(String text) {
        if (text.isEmpty()) {
            return List.of();
        }
        return itemRepository.getItems().stream()
                .filter(item -> item.hasContainsText(text))
                .filter(Item::isAvailable)
                .map(ItemMapper::mapToItemResponseDto)
                .toList();
    }

    private long getItemId() {
        return id++;
    }
}

