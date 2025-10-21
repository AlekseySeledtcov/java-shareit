package ru.practicum.shareit.interfaces;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserMapperTest {
    @Autowired
    private UserMapper userMapper;

    private UserRequestDto userRequestDto;
    private User user;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setup() {
        userRequestDto = new UserRequestDto();
        userRequestDto.setName("User name");
        userRequestDto.setEmail("User@mail.com");

        user = new User();
        user.setId(1L);
        user.setName("User name");
        user.setEmail("User@mail.com");

        userResponseDto = new UserResponseDto();
        userResponseDto.setId(1L);
        userResponseDto.setName("User name");
        userResponseDto.setEmail("User@mail.com");
    }

    @Test
    void toEntity() {
        User actual = userMapper.toEntity(userRequestDto);

        assertEquals(userRequestDto.getName(), actual.getName());
        assertEquals(userRequestDto.getEmail(), actual.getEmail());
    }

    @Test
    void toDto() {
        UserResponseDto actual = userMapper.toDto(user);

        assertEquals(userRequestDto.getName(), actual.getName());
        assertEquals(userRequestDto.getEmail(), actual.getEmail());
    }

    @Test
    void updateField() {
        user.setName("Old Name");
        userRequestDto.setName(null);
        userRequestDto.setEmail("new@email.com");

        userMapper.updateField(userRequestDto, user);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("Old Name");
        assertThat(user.getEmail()).isEqualTo("new@email.com");
    }
}