package ru.practicum.shareit.interfaces;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

@Named("ItemMapperUtil")
@Component
@RequiredArgsConstructor
public class ItemMapperUtil {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Named("ItemIdToItem")
    public Item mapItemIdToItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Вещь не найдена"));
    }

    @Named("mapItem")
    public ItemResponseDto mapItem(Item item) {
        return itemMapper.toDto(item);
    }
}
