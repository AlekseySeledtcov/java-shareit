package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingRequestDtoJsonTest {
    private final JacksonTester<BookingRequestDto> json;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Test
    void bookingRequestDtoTest() {
        // Подгатавливаем данные
        String start = LocalDateTime.of(2025, 10, 25, 19, 25, 0)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String end = LocalDateTime.of(2025, 10, 25, 19, 35, 0)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // Создаем Json строку
        Map<String, Object> jsonMap = Map.of(
                "id", 1,
                "start", start,
                "end", end,
                "itemId", 2,
                "booker", 3,
                "status", "WAITING"
        );

        String jsonString = objectMapper.writeValueAsString(jsonMap);

        // Десериализуем
        BookingRequestDto deserialized = json.parseObject(jsonString);

        assertThat(deserialized.getId()).isEqualTo(1L);
        assertThat(deserialized.getStart()).isEqualTo(LocalDateTime
                .of(2025, 10, 25, 19, 25, 0));
        assertThat(deserialized.getEnd()).isEqualTo(LocalDateTime
                .of(2025, 10, 25, 19, 35, 0));
        assertThat(deserialized.getItemId()).isEqualTo(2);
        assertThat(deserialized.getBooker()).isEqualTo(3);
        assertThat(deserialized.getStatus()).isEqualTo(Status.WAITING);

    }

}