package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final HashMap<Long, Item> inMemoryItemRepository = new HashMap<>();

    @Override
    public Optional<Item> postItem(Item item) {
        inMemoryItemRepository.put(item.getId(), item);
        return getItemById(item.getId());
    }

    @Override
    public Optional<Item> getItemById(Long itemId) {
        return Optional.ofNullable(inMemoryItemRepository.get(itemId));
    }


    @Override
    public List<Item> getItems() {
        return new ArrayList<>(inMemoryItemRepository.values());
    }

    @Override
    public List<Item> getItemByText(String text) {
        return List.of();
    }
}
