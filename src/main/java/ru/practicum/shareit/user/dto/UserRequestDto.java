package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import ru.practicum.shareit.interfaces.OnCreateGroup;
import ru.practicum.shareit.interfaces.OnPatchGroup;

@Getter
@Setter
public class UserRequestDto {

    @NotBlank(groups = OnCreateGroup.class, message = "Имя пользователя должно быть указано")
    @Length(min = 5, max = 50, groups = {OnCreateGroup.class, OnPatchGroup.class})
    private String name;

    @Email(groups = {OnCreateGroup.class, OnPatchGroup.class}, message = "Указан не корректный адрес электронной почты")
    @Email(groups = {OnCreateGroup.class}, message = "Указан не корректный адрес электронной почты")
    private String email;
}