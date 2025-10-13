//package ru.practicum.shareit.item;
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
//import ru.practicum.shareit.item.dto.CommentDto;
//import ru.practicum.shareit.item.dto.ItemRequestDto;
//import ru.practicum.shareit.item.dto.ItemResponseDto;
//import ru.practicum.shareit.item.dto.ItemResponseWithBookingDateDto;
//
//import java.nio.charset.StandardCharsets;
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//
//@WebMvcTest(controllers = ItemController.class)
//class ItemControllerTest {
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    private ItemRequestDto itemRequestDto;
//    private ItemResponseDto itemResponseDto;
//    private ItemResponseWithBookingDateDto itemResponseWithBookingDateDto;
//    private CommentDto commentDto;
//
//    @BeforeEach
//    void setUp() {
//        itemRequestDto = new ItemRequestDto();
//        itemRequestDto.setName("ItemName");
//        itemRequestDto.setDescription("Description");
//        itemRequestDto.setAvailable(true);
//        itemRequestDto.setOwner(1L);
//
//        itemResponseDto = new ItemResponseDto();
//        itemResponseDto.setId(1L);
//        itemResponseDto.setName("ItemName");
//        itemResponseDto.setDescription("Description");
//        itemResponseDto.setAvailable(true);
//        itemResponseDto.setRequest(2L);
//
//        itemResponseWithBookingDateDto = new ItemResponseWithBookingDateDto();
//        itemResponseWithBookingDateDto.setId(1L);
//        itemResponseWithBookingDateDto.setName("ItemName");
//        itemResponseWithBookingDateDto.setDescription("ItemDescription");
//        itemResponseWithBookingDateDto.setAvailable(true);
//        itemResponseWithBookingDateDto.setLastBooking(LocalDateTime.now().minusMinutes(10));
//        itemResponseWithBookingDateDto.setNextBooking(LocalDateTime.now());
//        itemResponseWithBookingDateDto.setComments(List.of(new CommentDto()));
//
//        commentDto = new CommentDto();
//        commentDto.setId(1L);
//        commentDto.setText("CommentText");
//        commentDto.setAuthorName("AuthorName");
//        commentDto.setCreated(LocalDateTime.now());
//    }
//
//    @SneakyThrows
//    @Test
//    void postItemWhenItemRequestIsNotValidThenReturnedBAdRequest() {
//        long userId = 1L;
//        itemRequestDto.setDescription("");
//
//        mockMvc.perform(post("/items")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .content(objectMapper.writeValueAsString(itemRequestDto))
//                        .header(RequestHeaders.USER_ID, userId)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("violations").isNotEmpty());
//    }
//
//    @SneakyThrows
//    @Test
//    void patchItemWhenItemRequestIsNotValidThenReturnBadRequest() {
//        Long itemId = 1L;
//        Long userId = 1L;
//        itemRequestDto.setDescription("xx");
//
//        mockMvc.perform(patch("/items/{itemId}", itemId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .content(objectMapper.writeValueAsString(itemRequestDto))
//                        .header(RequestHeaders.USER_ID, userId)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("violations").isNotEmpty());
//    }
//
//    @SneakyThrows
//    @Test
//    void getItemByIdWhenItemRequestIsNotValidThenReturnBadRequest() {
//        Long itemId = -1L;
//        Long userId = 1L;
//
//        mockMvc.perform(get("/items/{itemId}", itemId)
//                        .header(RequestHeaders.USER_ID, userId)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//
//    }
//
//    @SneakyThrows
//    @Test
//    void getItemsWhenItemRequestIsNotValidThenReturnBadRequest() {
//        Long userId = -1L;
//
//        mockMvc.perform(get("/items", userId)
//                        .header(RequestHeaders.USER_ID, userId)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }
//
//    @SneakyThrows
//    @Test
//    void getItemByTextWhenItemRequestIsNotValidThenReturnBadRequest() {
//        Long userId = -1L;
//        String text = "text";
//
//        mockMvc.perform(get("/items/search?text={text}", text)
//                        .header(RequestHeaders.USER_ID, userId)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }
//
//    @SneakyThrows
//    @Test
//    void postCommentWhenItemRequestIsNotValidThenReturnBadRequest() {
//        Long itemId = 1L;
//        Long userId = 1L;
//        commentDto.setText("");
//
//        mockMvc.perform(post("/items/{itemId}/comment", itemId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .content(objectMapper.writeValueAsString(commentDto))
//                        .header(RequestHeaders.USER_ID, userId)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("violations").isNotEmpty());
//    }
//}