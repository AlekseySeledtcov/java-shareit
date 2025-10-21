package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemTest {

    @Test
    void testBasicItemCreation() {
        // Создаем базовый объект
        Item item = new Item();

        // Проверяем значения по умолчанию
        assertEquals(0L, item.getId());
        assertNull(item.getName());
        assertNull(item.getDescription());
        assertNull(item.getAvailable());
        assertNull(item.getOwner());
        assertNull(item.getRequest());
    }

    @Test
    void testFullItemCreation() {
        // Создаем тестовые данные
        User owner = new User();
        owner.setId(1L);

        ItemRequest request = new ItemRequest();
        request.setId(2L);

        // Создаем объект с заполненными полями
        Item item = new Item();
        item.setId(100L);
        item.setName("Test Item");
        item.setDescription("Описание предмета");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(request);

        // Проверяем корректность установки значений
        assertEquals(100L, item.getId());
        assertEquals("Test Item", item.getName());
        assertEquals("Описание предмета", item.getDescription());
        assertTrue(item.getAvailable());
        assertEquals(owner, item.getOwner());
        assertEquals(request, item.getRequest());
    }

    @Test
    void testToStringMethod() {
        // Создаем объект для проверки toString
        Item item = new Item();
        item.setId(100L);
        item.setName("Test Item");

        // Проверяем базовую структуру строки
        assertTrue(item.toString().contains("Item"));
        assertTrue(item.toString().contains("id=" + item.getId()));
        assertTrue(item.toString().contains("name=" + item.getName()));
    }

    @Test
    void testEqualsAndHashCode() {
        // Создаем два одинаковых объекта
        Item item1 = new Item();
        item1.setId(100L);
        item1.setName("Test Item");

        Item item2 = new Item();
        item2.setId(100L);
        item2.setName("Test Item");

        // Проверяем equals и hashCode
        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());

        // Меняем одно поле
        item2.setName("Another Name");

        // Проверяем, что объекты больше не равны
        assertNotEquals(item1, item2);
        assertNotEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    void testNullFields() {
        // Создаем объект с null полями
        Item item = new Item();
        item.setAvailable(null);
        item.setOwner(null);
        item.setRequest(null);

        // Проверяем корректность работы с null
        assertNull(item.getAvailable());
        assertNull(item.getOwner());
        assertNull(item.getRequest());
    }
}