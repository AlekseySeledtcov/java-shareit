package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private UserResponseDto userResponseDto;
    private UserRequestDto userRequestDto;

    @BeforeEach
    void setup() {
        userResponseDto = new UserResponseDto();
        userResponseDto.setId(1L);
        userResponseDto.setName("UserName");
        userResponseDto.setEmail("UserName@mail.ru");

        userRequestDto = new UserRequestDto();
        userRequestDto.setName("UserName");
        userRequestDto.setEmail("UserName@mail.ru");
    }

    @SneakyThrows
    @Test
    void postUserWhenUserRequestIsValidThenPostUser() {
        when(userService.postUser(any())).thenReturn(userResponseDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(userRequestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(userResponseDto.getId()), Long.class))
                .andExpect(jsonPath("name", is(userRequestDto.getName()), String.class))
                .andExpect(jsonPath("email", is(userRequestDto.getEmail()), String.class));
        verify(userService, times(1)).postUser(any(UserRequestDto.class));
    }

    @SneakyThrows
    @Test
    void postUserWhenUserRequestNotValidThenReturnedBadRequest() {
        userRequestDto.setName("");

        when(userService.postUser(any())).thenReturn(userResponseDto);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(objectMapper.writeValueAsString(userRequestDto))
                .accept(MediaType.APPLICATION_JSON));
        verify(userService, never()).postUser(any(UserRequestDto.class));
    }

    @SneakyThrows
    @Test
    void patchUserWhenUserRequestIsValidThenPatchUser() {
        long userId = 1L;
        userRequestDto.setName("PatchedName");
        userResponseDto.setName("PatchedName");

        when(userService.patchUser(any(), anyLong())).thenReturn(userResponseDto);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(userRequestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(userResponseDto.getId()), Long.class))
                .andExpect(jsonPath("name", is(userResponseDto.getName()), String.class))
                .andExpect(jsonPath("email", is(userResponseDto.getEmail()), String.class));
        verify(userService, times(1)).patchUser(any(UserRequestDto.class), anyLong());
    }

    @SneakyThrows
    @Test
    void patchUserWhenUserRequestIsNotValidThenReturnBadRequest() {
        long userId = 1L;
        userRequestDto.setEmail("mail");

        when(userService.patchUser(any(), anyLong())).thenReturn(userResponseDto);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(userRequestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).patchUser(any(UserRequestDto.class), anyLong());
    }

    @SneakyThrows
    @Test
    void getUser() {
        long userId = 1L;

        when(userService.getUser(anyLong())).thenReturn(userResponseDto);

        mockMvc.perform(get("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(userRequestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(userResponseDto.getId()), Long.class))
                .andExpect(jsonPath("name", is(userResponseDto.getName()), String.class))
                .andExpect(jsonPath("email", is(userResponseDto.getEmail()), String.class));
        verify(userService, times(1)).getUser(anyLong());
    }

    @SneakyThrows
    @Test
    void deleteUser() {
       /* to doo*/
//        long userId = 1L;
//
//        when(userService.deleteUser(anyLong())).thenAnswer(status().isOk());
//        mockMvc.perform(delete("/users/userId", userId))
//                .andExpect(status().isOk());
//        verify(userService, times(1)).deleteUser(anyLong());
    }




    @SneakyThrows
    @Test
    void getUsers() {
        when(userService.getUsers()).thenReturn(List.of(userResponseDto));

        mockMvc.perform(get("/users")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userService, times(1)).getUsers();
    }
}