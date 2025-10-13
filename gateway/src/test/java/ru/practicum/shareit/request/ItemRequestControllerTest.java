//package ru.practicum.shareit.request;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.SneakyThrows;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import ru.practicum.shareit.common.RequestHeaders;
//import ru.practicum.shareit.item.dto.ItemResponseWithOwnerIdDto;
//import ru.practicum.shareit.request.dto.ItemRequestDto;
//import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
//
//import java.nio.charset.StandardCharsets;
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(controllers = ItemRequestController.class)
//class ItemRequestControllerTest {
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    private ItemRequestDto requestDto;
//    private ItemRequestWithItemsDto requestWithItemsDto;
//
//    @BeforeEach
//    void setup() {
//        requestDto = new ItemRequestDto();
//        requestDto.setId(1L);
//        requestDto.setDescription("Description");
//        requestDto.setCreated(LocalDateTime.of(2025, 10, 9, 11, 0, 0));
//
//        requestWithItemsDto = new ItemRequestWithItemsDto();
//        requestWithItemsDto.setId(1l);
//        requestWithItemsDto.setDescription("Description");
//        requestWithItemsDto.setRequestor(1L);
//        requestWithItemsDto.setCreated(LocalDateTime.of(2025, 10, 9, 11, 0, 0));
//        requestWithItemsDto.setItemResponseWithOwnerIdDtos(List.of(new ItemResponseWithOwnerIdDto()));
//    }
//
//    @SneakyThrows
//    @Test
//    void postItemRequestWhenItemRequestNotValidThenReturnedBadRequest() {
//        long requestorId = 1L;
//        requestDto.setDescription(null);
//
//        mockMvc.perform(post("/requests")
//                        .content(objectMapper.writeValueAsString(requestDto)) // Задает тело запроса
//                        .characterEncoding(StandardCharsets.UTF_8) //позволяет задать кодировку символов для теля запроса
//                        .contentType(MediaType.APPLICATION_JSON) // задает тип контента для теля запроса
//                        .header(RequestHeaders.USER_ID, requestorId) // позволяет задать заголовок (header) для запроса
//                        .accept(MediaType.APPLICATION_JSON)) // позволяет указать тип контента который клиент ожидает получить от сервера
//                .andExpect(status().isBadRequest());
//    }
//
//    @SneakyThrows
//    @Test
//    void getItemRequestWhenItemRequestNotValidThenReturnedBadRequest() {
//        Long requestorId = -1L;
//
//        mockMvc.perform(get("/requests")
//                        .header(RequestHeaders.USER_ID, requestorId)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }
//
//    @SneakyThrows
//    @Test
//    void getItemRequestAllWhenItemRequestNotValidThenReturnedBadRequest() {
//        Long requestor = -1L;
//
//        mockMvc.perform(get("/requests/all")
//                        .header(RequestHeaders.USER_ID, requestor)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }
//
//    @SneakyThrows
//    @Test
//    void getRequestById() {
//        long requestId = -1L;
//
//        mockMvc.perform(get("/requests/{requestId}", requestId)
//                        .header(RequestHeaders.USER_ID, requestId)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }
//}