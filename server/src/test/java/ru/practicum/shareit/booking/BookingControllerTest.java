package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.constants.RequestHeaders;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookingService bookingService;

    private BookingRequestDto bookingRequestDto;
    private BookingResponseDto bookingResponseDto;

    @BeforeEach
    void setup() {
        bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setId(1L);
        bookingRequestDto.setStart(LocalDateTime.now());
        bookingRequestDto.setEnd(LocalDateTime.now().plusMinutes(10));
        bookingRequestDto.setId(1L);
        bookingRequestDto.setBooker(1L);

        bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setId(1L);
        bookingResponseDto.setStart(LocalDateTime.now());
        bookingResponseDto.setEnd(LocalDateTime.now().plusMinutes(10));
        bookingResponseDto.setId(1L);
        bookingResponseDto.setBooker(new UserResponseDto());
        bookingResponseDto.setStatus(Status.APPROVED);

    }

    @SneakyThrows
    @Test
    void postBooking() {
        Long userId = 1L;

        when(bookingService.postBooking(anyLong(), any())).thenReturn(bookingResponseDto);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .header(RequestHeaders.USER_ID, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingResponseDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingResponseDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.item", is(bookingResponseDto.getItem())))
                .andExpect(jsonPath("$.booker.id", is(bookingResponseDto.getBooker().getId())))
                .andExpect(jsonPath("$.booker.name", is(bookingResponseDto.getBooker().getId())))
                .andExpect(jsonPath("$.booker.email", is(bookingResponseDto.getBooker().getId())))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().name())));
        verify(bookingService, times(1)).postBooking(anyLong(), any(BookingRequestDto.class));
    }

    @SneakyThrows
    @Test
    void patchBooking() {
        Long userId = 1L;
        Long bookingId = 1L;
        Boolean approved = true;

        when(bookingService.patchBooking(anyLong(), anyBoolean(), anyLong())).thenReturn(bookingResponseDto);

        mockMvc.perform(patch("/bookings/{bookingId}?approved={approved}", bookingId, approved)
                        .header(RequestHeaders.USER_ID, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingResponseDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingResponseDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.item", is(bookingResponseDto.getItem())))
                .andExpect(jsonPath("$.booker.id", is(bookingResponseDto.getBooker().getId())))
                .andExpect(jsonPath("$.booker.name", is(bookingResponseDto.getBooker().getId())))
                .andExpect(jsonPath("$.booker.email", is(bookingResponseDto.getBooker().getId())))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().name())));
        verify(bookingService, times(1)).patchBooking(anyLong(), anyBoolean(), anyLong());
    }

    @SneakyThrows
    @Test
    void getBookingDataById() {
        Long userId = 1L;
        Long bookingId = 1L;

        when(bookingService.getBookingDataById(anyLong(), anyLong())).thenReturn(bookingResponseDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(RequestHeaders.USER_ID, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingResponseDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingResponseDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.item", is(bookingResponseDto.getItem())))
                .andExpect(jsonPath("$.booker.id", is(bookingResponseDto.getBooker().getId())))
                .andExpect(jsonPath("$.booker.name", is(bookingResponseDto.getBooker().getId())))
                .andExpect(jsonPath("$.booker.email", is(bookingResponseDto.getBooker().getId())))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().name())));
        verify(bookingService, times(1)).getBookingDataById(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void getBookingByState() {
        Long userId = 1L;
        BookingState state = BookingState.WAITING;

        when(bookingService.getBookingByStateCurrentUser(any(), anyLong())).thenReturn(List.of(bookingResponseDto));

        mockMvc.perform(get("/bookings?state={state}", state)
                        .header(RequestHeaders.USER_ID, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingResponseDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].end", is(bookingResponseDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].item", is(bookingResponseDto.getItem())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingResponseDto.getBooker().getId())))
                .andExpect(jsonPath("$[0].booker.name", is(bookingResponseDto.getBooker().getId())))
                .andExpect(jsonPath("$[0].booker.email", is(bookingResponseDto.getBooker().getId())))
                .andExpect(jsonPath("$[0].status", is(bookingResponseDto.getStatus().name())));
        verify(bookingService, times(1)).getBookingByStateCurrentUser(any(), anyLong());
    }

    @SneakyThrows
    @Test
    void getBookingByStateCurrentOwner() {
        Long userId = 1L;
        BookingState state = BookingState.CURRENT;

        when(bookingService.getBookingByStateCurrentOwner(any(), anyLong())).thenReturn(List.of(bookingResponseDto));

        mockMvc.perform(get("/bookings/owner?state={state}", state)
                        .header(RequestHeaders.USER_ID, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingResponseDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].end", is(bookingResponseDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].item", is(bookingResponseDto.getItem())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingResponseDto.getBooker().getId())))
                .andExpect(jsonPath("$[0].booker.name", is(bookingResponseDto.getBooker().getId())))
                .andExpect(jsonPath("$[0].booker.email", is(bookingResponseDto.getBooker().getId())))
                .andExpect(jsonPath("$[0].status", is(bookingResponseDto.getStatus().name())));
        verify(bookingService, times(1)).getBookingByStateCurrentOwner(any(), anyLong());
    }
}