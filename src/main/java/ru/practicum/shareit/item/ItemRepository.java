package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Optional<Item> postItem(Item item);

    Optional<Item> getItemById(Long itemId);

    List<Item> getItems();

    List<Item> getItemByText(String text);
}
