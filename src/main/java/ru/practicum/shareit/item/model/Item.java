package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Item {
    private long id;
    @NotBlank(message = "Имя должно быть указано")
    private String name;
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
    @NotNull(message = "Статус должен быть проставлен")
    private boolean available;
    @NotNull(message = "Автор должен быть указан")
    private long owner;
    private long request;

    public boolean hasContainsText(String text) {
        text = text.toLowerCase().replaceAll("\\s+", "");
        return (name.toLowerCase().replaceAll("\\s+", "").contains(text) ||
                description.toLowerCase().replaceAll("\\s+", "").contains(text));
    }
}
