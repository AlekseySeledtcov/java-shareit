package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import ru.practicum.shareit.interfaces.OnCreateGroup;
import ru.practicum.shareit.interfaces.OnPatchGroup;

@Getter
@Setter
public class ItemRequestDto {
    @NotBlank(groups = OnCreateGroup.class, message = "Название вещи не может быть пустым")
    private String name;

    @NotBlank(groups = OnCreateGroup.class, message = "Описание не может быть пустым")
    @Length(min = 5, max = 200, groups = {OnCreateGroup.class, OnPatchGroup.class})
    private String description;

    @NotNull(groups = OnCreateGroup.class)
    private Boolean available;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long owner;


    private Long requestId;
}

