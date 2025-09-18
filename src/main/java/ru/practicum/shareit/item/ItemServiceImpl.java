package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.interfaces.ItemMapper;
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
    private final ItemMapper itemMapper;
    private long id = 1L;

    @Override
    public ItemResponseDto postItem(ItemRequestDto itemRequestDto) {
        log.debug("postItem. Добавление вещи ");
        Item item = itemMapper.toEntity(itemRequestDto);
        item.setId(getItemId());
        userService.getUser(item.getOwner());
        return itemMapper.toDto(itemRepository.postItem(item));
    }

    @Override
    public ItemResponseDto patchItem(Long itemId, Long userId, ItemRequestDto itemRequestDto) {
        if (!itemRepository.containsItem(itemId)) {
            throw new EntityNotFoundException("Вещь не найдена");
        }

        Item oldItem = itemRepository.getItemById(itemId);

        if (oldItem.getOwner() != userId) {
            throw new EntityNotFoundException("Вещь не найдена");
        }

        Item newItem = itemMapper.updateField(itemRequestDto, oldItem);
        newItem.setOwner(userId);

        return itemMapper.toDto(itemRepository.postItem(newItem));
    }

    @Override
    public ItemResponseDto getItemById(Long itemId, Long userId) {
        if (!itemRepository.containsItem(itemId)) {
            throw new EntityNotFoundException("Вещь не найдена");
        }
        Item item = itemRepository.getItemById(itemId);
        if (item.getOwner() != userId) {
            throw new EntityNotFoundException("Вещь не указанному userId хозяина");
        }
        return itemMapper.toDto(item);
    }

    @Override
    public List<ItemResponseDto> getItems(Long userId) {
        return itemRepository.getItems().stream()
                .filter(item -> item.getOwner() == userId)
                .map(itemMapper::toDto)
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
                .map(itemMapper::toDto)
                .toList();
    }

    private long getItemId() {
        return id++;
    }
}