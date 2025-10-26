package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exceptions.AlreadyExistsException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.interfaces.UserMapper;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequestDto userRequestDto;
    private User user;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setup() {
        userRequestDto = new UserRequestDto();
        userRequestDto.setName("Иван");
        userRequestDto.setEmail("ivan@mail.ru");

        user = new User(1L, "Иван");
        user.setEmail("ivan@mail.ru");

        userResponseDto = new UserResponseDto();
        userResponseDto.setId(1L);
        userResponseDto.setName("Иван");
        userResponseDto.setEmail("ivan@mail.ru");
    }

    @Test
    void postUserIfUserNotExistInDB() {
        UserResponseDto expected = userResponseDto;
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toEntity(userRequestDto)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        UserResponseDto actual = userService.postUser(userRequestDto);

        assertEquals(expected, actual);
        verify(userMapper, times(1)).toDto(any());
    }

    @Test
    void postUserIfUserExistInDB() {
        when(userRepository.findByEmail(userRequestDto.getEmail())).thenReturn(Optional.of(user));

        assertThrows(AlreadyExistsException.class, () -> userService.postUser(userRequestDto));
        verify(userMapper, never()).toDto(any());
    }


    @Test
    void patchUser() {
        Long userId = 1L;
        UserResponseDto expectUser = userResponseDto;
        expectUser.setName("Петр");
        userRequestDto.setName("Петр");

        when(userRepository.findByEmail(userRequestDto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        doAnswer(invocation -> {
            UserRequestDto userRequestDto1 = invocation.getArgument(0);
            User user = invocation.getArgument(1);
            user.setName(userRequestDto1.getName());
            return null;
        }).when(userMapper).updateField(any(UserRequestDto.class), any(User.class));
        when(userService.patchUser(userRequestDto, userId)).thenReturn(expectUser);

        UserResponseDto actual = userService.patchUser(userRequestDto, userId);
        assertEquals(expectUser, actual);
        verify(userMapper, times(1)).toDto(any());

    }

    @Test
    void patchUserIfTheAddedUserIsExistInDataBaseThenThrowException() {
        Long userId = 2L;

        when(userRepository.findByEmail(userRequestDto.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        assertThrows(AlreadyExistsException.class, () -> userService.patchUser(userRequestDto, userId));
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void getUserIfUserExist() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        UserResponseDto actual = userService.getUser(userId);

        assertEquals(userResponseDto, actual);
        verify(userMapper, times(1)).toDto(any());
    }

    @Test
    void getUserIfUserNotExistThenTrowException() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            userService.getUser(userId);
        });
        verify(userMapper, never()).toDto(any());
    }


    @Test
    void deleteUser() {
        Long userId = 1L;

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void getUsers() {
        User user1 = new User(1L, "User 1");
        User user2 = new User(2L, "User 2");
        UserResponseDto userResponseDto1 = new UserResponseDto(1L, "User 1");
        UserResponseDto userResponseDto2 = new UserResponseDto(2L, "User 2");

        Page<User> userPage = new PageImpl<>(List.of(user1, user2));

        when(userRepository.findAll(any(PageRequest.class))).thenReturn(userPage);
        when(userMapper.toDto(user1)).thenReturn(userResponseDto1);
        when(userMapper.toDto(user2)).thenReturn(userResponseDto2);

        List<UserResponseDto> users = userService.getUsers();
        assertEquals(2, users.size());
        assertEquals(1L, (long) users.get(0).getId());
        assertEquals(2L, (long) users.get(1).getId());
    }
}