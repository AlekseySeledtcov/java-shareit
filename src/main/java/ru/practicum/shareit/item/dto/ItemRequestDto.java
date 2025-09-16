package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.practicum.shareit.interfaces.OnCreateGroup;
import ru.practicum.shareit.interfaces.OnPatchGroup;
import ru.practicum.shareit.item.model.Item;

@Data
public class ItemRequestDto {
    @NotBlank(groups = OnCreateGroup.class, message = "Имя должно быть указано")
    @Length(min = 5, max = 50, groups = {OnCreateGroup.class, OnPatchGroup.class})
    private String name;
    @NotBlank(groups = OnCreateGroup.class, message = "Описание не может быть пустым")
    @Length(min = 5, max = 200, groups = {OnCreateGroup.class, OnPatchGroup.class})
    private String description;
    @NotNull(groups = OnCreateGroup.class, message = "Статус должен быть проставлен")
    private Boolean available;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long owner;

    private boolean hasName() {
        return !(name == null || name.isBlank());
    }

    private boolean hasDescription() {
        return !(description == null || description.isBlank());
    }

    private boolean hasAvailable() {
        return available != null;
    }

    public static Item updateItemField(ItemRequestDto itemRequestDto, Item item) {
        if (itemRequestDto.hasName()) {
            item.setName(itemRequestDto.getName());
        }
        if (itemRequestDto.hasDescription()) {
            item.setDescription(itemRequestDto.getDescription());
        }

        if (itemRequestDto.hasAvailable()) {
            item.setAvailable(itemRequestDto.getAvailable());
        }

        return item;
    }
}
