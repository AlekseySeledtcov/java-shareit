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
import ru.practicum.shareit.common.RequestHeaders;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @MockBean
    BookingClient bookingClient;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mockMvc;

    private BookingRequestDto bookingRequestDto;

    @BeforeEach
    void setup() {
        bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setId(1L);
        bookingRequestDto.setStart(LocalDateTime.now());
        bookingRequestDto.setEnd(LocalDateTime.now().plusMinutes(10L));
        bookingRequestDto.setItemId(1L);
        bookingRequestDto.setBooker(1L);
    }


    @SneakyThrows
    @Test
    void postBookingWhenBookingRequestNotValidThenReturnedBadRequest() {
        Long userId = 1L;
        bookingRequestDto.setItemId(null);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .header(RequestHeaders.USER_ID, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").isNotEmpty());
    }

    @SneakyThrows
    @Test
    void patchBookingWhenBookingRequestNotValidThenReturnedBadRequest() {
        Long userId = 1L;
        Long bookingId = 1L;
        Boolean approved = null;

        mockMvc.perform(patch("/bookings/{bookingId}?approved={approved}", bookingId, approved)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .header(RequestHeaders.USER_ID, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @SneakyThrows
    @Test
    void getBookingDataByIdWhenBookingRequestNotValidThenReturnedBadRequest() {
        Long userId = -1L;
        Long bookingId = 1L;

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(RequestHeaders.USER_ID, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getBookingByStateWhenBookingRequestNotValidThenReturnedBadRequest() {
        Long userId = 1L;
        String stringState = "AL";

        mockMvc.perform(get("/bookings?state={state}", stringState)
                        .header(RequestHeaders.USER_ID, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getBookingByStateCurrentOwnerWhenBookingRequestNotValidThenReturnedBadRequest() {
        Long userId = -11L;
        String stringState = "ALL";

        mockMvc.perform(get("/bookings/owner?state={state}", stringState)
                        .header(RequestHeaders.USER_ID, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }
}