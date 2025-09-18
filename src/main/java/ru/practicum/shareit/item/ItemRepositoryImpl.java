package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> inMemoryItemRepository = new HashMap<>();

    @Override
    public Item postItem(Item item) {
        inMemoryItemRepository.put(item.getId(), item);
        return getItemById(item.getId());
    }

    @Override
    public Item getItemById(Long itemId) {
        return inMemoryItemRepository.get(itemId);
    }

    @Override
    public List<Item> getItems() {
        return new ArrayList<>(inMemoryItemRepository.values());
    }

    @Override
    public boolean containsItem(Long id) {
        return inMemoryItemRepository.containsKey(id);
    }
}