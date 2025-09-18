package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item postItem(Item item);

    Item getItemById(Long itemId);

    List<Item> getItems();

    boolean containsItem(Long id);
}
