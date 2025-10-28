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

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerITest {

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
        requestDto.setRequestor(1L);
        requestDto.setCreated(LocalDateTime.of(2025, 10, 9, 11, 0, 0));

        ItemResponseWithOwnerIdDto itemResponseWithOwnerIdDto = new ItemResponseWithOwnerIdDto();
        itemResponseWithOwnerIdDto.setId(1L);
        itemResponseWithOwnerIdDto.setOwnerId(1L);
        itemResponseWithOwnerIdDto.setName("Name");

        requestWithItemsDto = new ItemRequestWithItemsDto();
        requestWithItemsDto.setId(1L);
        requestWithItemsDto.setDescription("Description");
        requestWithItemsDto.setRequestor(1L);
        requestWithItemsDto.setCreated(LocalDateTime.of(2025, 10, 9, 11, 0, 0));
        requestWithItemsDto.setItems(List.of(itemResponseWithOwnerIdDto));
    }

    @SneakyThrows
    @Test
    void posetItemRequestWhenItemRequestIsValidThenPostItemRequest() {
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
    void getItemRequestsWhenItemRequestIsValidThenGetItemRequests() {
        Long requestorId = 1L;

        when(itemRequestService.getItemRequest(anyLong())).thenReturn(List.of(requestWithItemsDto));

        mockMvc.perform(get("/requests")
                        .header(RequestHeaders.USER_ID, requestorId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(requestWithItemsDto))))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].description").exists())
                .andExpect(jsonPath("$[0].requestor").exists())
                .andExpect(jsonPath("$[0].created").exists())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
        verify(itemRequestService, times(1)).getItemRequest(requestorId);
    }

    @SneakyThrows
    @Test
    void getItemRequestAllWhenItemRequestIsValidThenGetItemRequests() {
        Long userId = 1L;
        when(itemRequestService.getItemRequestAll(anyLong())).thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests/all")
                        .header(RequestHeaders.USER_ID, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(requestDto))))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].description").exists())
                .andExpect(jsonPath("$[0].requestor").exists())
                .andExpect(jsonPath("$[0].created").exists());
        verify(itemRequestService, times(1)).getItemRequestAll(userId);
    }

    @SneakyThrows
    @Test
    void getRequestByIdWhenItemRequestIsValidThenGetItemRequest() {
        Long userId = 1L;
        long requestId = 1L;

        when(itemRequestService.getRequestById(anyLong(), anyLong())).thenReturn(requestWithItemsDto);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(RequestHeaders.USER_ID, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requestWithItemsDto)))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.requestor").exists())
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.items", hasSize(greaterThan(0))));
        verify(itemRequestService, times(1)).getRequestById(userId, requestId);
    }
}