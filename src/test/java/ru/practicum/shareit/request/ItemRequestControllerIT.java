package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.constants.RequestHeaders;
import ru.practicum.shareit.item.dto.ItemResponseWithOwnerIdDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerIT {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    private ItemRequestDto requestDto;
    private ItemRequestWithItemsDto requestWithItemsDto;

    @BeforeEach
    void setup() {
        requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("Description");
        requestDto.setCreated(LocalDateTime.of(2025,10,9,11,0,0));

        requestWithItemsDto = new ItemRequestWithItemsDto();
        requestWithItemsDto.setId(1l);
        requestWithItemsDto.setDescription("Description");
        requestWithItemsDto.setRequestor(1L);
        requestWithItemsDto.setCreated(LocalDateTime.of(2025, 10, 9, 11, 0, 0));
        requestWithItemsDto.setItemResponseWithOwnerIdDtos(List.of(new ItemResponseWithOwnerIdDto()));
    }


    @SneakyThrows
    @Test
    void poseItemRequestWhenItemRequestIsValidThenPostItemRequest() {
        long requestorId = 1L;

        when(itemRequestService.postItemRequest(anyLong(), any()))
                .thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(RequestHeaders.USER_ID, requestorId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("description", is(requestDto.getDescription()), String.class))
                .andExpect(jsonPath("requestor", is(requestDto.getRequestor()), Long.class))
                .andExpect(jsonPath("created", is(requestDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME)), String.class));
    }

    @SneakyThrows
    @Test
    void postItemRequestWhenItemRequestNotValidThenReturnedBadRequest() {
        long requestorId = 1L;
        requestDto.setDescription(null);


        when(itemRequestService.postItemRequest(anyLong(), any()))
                .thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(requestDto)) // Задает тело запроса
                        .characterEncoding(StandardCharsets.UTF_8) //позволяет задать кодировку символов для теля запроса
                        .contentType(MediaType.APPLICATION_JSON) // задает тип контента для теля запроса
                        .header(RequestHeaders.USER_ID, 1L) // позволяет задать заголовок (header) для запроса
                        .accept(MediaType.APPLICATION_JSON)) // позволяет указать тип контента который клиент ожидает получить от сервера
                .andExpect(status().isBadRequest());
        verify(itemRequestService, never()).postItemRequest(requestorId, requestDto);
    }


    @SneakyThrows
    @Test
    void getItemRequest() {
        Long requestorId = 1L;

        when(itemRequestService.getItemRequest(anyLong())).thenReturn(List.of(requestWithItemsDto));

        mockMvc.perform(get("/requests")
                        .header(RequestHeaders.USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(requestWithItemsDto))));
        verify(itemRequestService, times(1)).getItemRequest(requestorId);
    }

    @SneakyThrows
    @Test
    void getItemRequestAll() {
        when(itemRequestService.getItemRequestAll()).thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests/all")
                        .header(RequestHeaders.USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(requestDto))));
        verify(itemRequestService, times(1)).getItemRequestAll();
    }

    @SneakyThrows
    @Test
    void getRequestById() {
        long requestId = 1L;

        when(itemRequestService.getRequestById(anyLong())).thenReturn(requestWithItemsDto);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(RequestHeaders.USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requestWithItemsDto)));

        verify(itemRequestService, times(1)).getRequestById(requestId);
    }
}