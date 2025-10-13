//package ru.practicum.shareit.item;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.SneakyThrows;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import ru.practicum.shareit.constants.RequestHeaders;
//import ru.practicum.shareit.item.dto.CommentDto;
//import ru.practicum.shareit.item.dto.ItemRequestDto;
//import ru.practicum.shareit.item.dto.ItemResponseDto;
//import ru.practicum.shareit.item.dto.ItemResponseWithBookingDateDto;
//
//import java.nio.charset.StandardCharsets;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//
//import static org.hamcrest.core.Is.is;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.*;
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
//    @MockBean
//    private ItemService itemService;
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
//    void postItemWhenItemRequestIsValidThenPostItem() {
//        long userId = 1L;
//
//        when(itemService.postItem(any())).thenReturn(itemResponseDto);
//
//        mockMvc.perform(post("/items")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .content(objectMapper.writeValueAsString(itemRequestDto))
//                        .header(RequestHeaders.USER_ID, userId)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("name", is(itemResponseDto.getName()), String.class))
//                .andExpect(jsonPath("description", is(itemResponseDto.getDescription()), String.class))
//                .andExpect(jsonPath("available", is(itemResponseDto.isAvailable()), Boolean.class))
//                .andExpect(jsonPath("request", is(itemResponseDto.getRequest()), Long.class));
//        verify(itemService, times(1)).postItem(any(ItemRequestDto.class));
//    }
//
////    @SneakyThrows
////    @Test
////    void postItemWhenItemRequestIsNotValidThenReturnedBAdRequest() {
////        long userId = 1L;
////        itemRequestDto.setDescription("");
////
////        when(itemService.postItem(any())).thenReturn(itemResponseDto);
////
////        mockMvc.perform(post("/items")
////                        .contentType(MediaType.APPLICATION_JSON)
////                        .characterEncoding(StandardCharsets.UTF_8)
////                        .content(objectMapper.writeValueAsString(itemRequestDto))
////                        .header(RequestHeaders.USER_ID, userId)
////                        .accept(MediaType.APPLICATION_JSON))
////                .andExpect(status().isBadRequest());
////        verify(itemService, never()).postItem(any(ItemRequestDto.class));
////    }
//
//    @SneakyThrows
//    @Test
//    void patchItemWhenItemRequestIsValidThenPatchItem() {
//        Long itemId = 1L;
//        Long userId = 1L;
//
//        when(itemService.patchItem(anyLong(), anyLong(), any())).thenReturn(itemResponseDto);
//
//        mockMvc.perform(patch("/items/{itemId}", itemId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .content(objectMapper.writeValueAsString(itemRequestDto))
//                        .header(RequestHeaders.USER_ID, userId)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("id", is(itemResponseDto.getId()), Long.class))
//                .andExpect(jsonPath("name", is(itemResponseDto.getName()), String.class))
//                .andExpect(jsonPath("description", is(itemResponseDto.getDescription()), String.class))
//                .andExpect(jsonPath("available", is(itemResponseDto.isAvailable()), Boolean.class))
//                .andExpect(jsonPath("request", is(itemResponseDto.getRequest()), Long.class));
//        verify(itemService, times(1)).patchItem(anyLong(), anyLong(), any(ItemRequestDto.class));
//    }
//
////    @SneakyThrows
////    @Test
////    void patchItemWhenItemRequestIsNotValidThenReturnBadRequest() {
////        Long itemId = 1L;
////        Long userId = 1L;
////        itemRequestDto.setDescription("xx");
////
////        when(itemService.patchItem(anyLong(), anyLong(), any())).thenReturn(itemResponseDto);
////
////        mockMvc.perform(patch("/items/{itemId}", itemId)
////                        .contentType(MediaType.APPLICATION_JSON)
////                        .characterEncoding(StandardCharsets.UTF_8)
////                        .content(objectMapper.writeValueAsString(itemRequestDto))
////                        .header(RequestHeaders.USER_ID, userId)
////                        .accept(MediaType.APPLICATION_JSON))
////                .andExpect(status().isBadRequest());
////        verify(itemService, never()).patchItem(anyLong(), anyLong(), any(ItemRequestDto.class));
////    }
//
//    @SneakyThrows
//    @Test
//    void getItemById() {
//        Long itemId = 1L;
//        Long userId = 1L;
//
//        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemResponseWithBookingDateDto);
//
//        mockMvc.perform(get("/items/{itemId}", itemId)
//                        .header(RequestHeaders.USER_ID, userId)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//        verify(itemService, times(1)).getItemById(anyLong(), anyLong());
//    }
//
//    @SneakyThrows
//    @Test
//    void getItems() {
//        Long userId = 1L;
//
//        when(itemService.getItems(anyLong())).thenReturn(List.of(itemResponseWithBookingDateDto));
//
//        mockMvc.perform(get("/items", userId)
//                        .header(RequestHeaders.USER_ID, userId)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//        verify(itemService, times(1)).getItems(anyLong());
//    }
//
//    @SneakyThrows
//    @Test
//    void getItemByText() {
//        String text = "text";
//
//        when(itemService.getItemByText(anyString())).thenReturn(List.of(itemResponseDto));
//
//        mockMvc.perform(get("/items/search?text={text}", text)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//        verify(itemService, times(1)).getItemByText(anyString());
//    }
//
//    @SneakyThrows
//    @Test
//    void postComment() {
//        Long itemId = 1L;
//        Long userId = 1L;
//
//        when(itemService.postComment(any(), anyLong(), anyLong())).thenReturn(commentDto);
//
//        mockMvc.perform(post("/items/{itemId}/comment", itemId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .content(objectMapper.writeValueAsString(commentDto))
//                        .header(RequestHeaders.USER_ID, userId)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("id", is(commentDto.getId()), Long.class))
//                .andExpect(jsonPath("text", is(commentDto.getText())))
//                .andExpect(jsonPath("authorName", is(commentDto.getAuthorName())))
//                .andExpect(jsonPath("created", is(commentDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME)), String.class));
//    }
//}