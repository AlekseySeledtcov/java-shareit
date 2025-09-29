package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByIdAndOwnerId(Long itemId, Long userId);

    @Query("SELECT i FROM Item AS i JOIN FETCH i.owner WHERE i.owner.id = :userId")
    Optional<Collection<Item>> findAllByUserId(Long userId);

    @Query("SELECT i FROM Item AS i " +
            "JOIN FETCH i.owner " +
            "WHERE (LOWER (i.name) LIKE LOWER(%:text%) OR LOWER (i.description) LIKE LOWER(%:text%)) AND i.available = true")
    Optional<Collection<Item>> findAllByNameOrDescriptionContaining(String text);

}
