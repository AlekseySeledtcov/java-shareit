package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingResponseDtoJsonTest {
    private final JacksonTester<BookingResponseDto> json;

    @SneakyThrows
    @Test
    void bookingRequestDtoTest() {
        // Подгатавливаем тестовые данные
        ItemResponseDto item = createItem();
        UserResponseDto booker = createBooker();

        // Создаем объект
        BookingResponseDto bookingResponseDto = new BookingResponseDto(
                1L,
                LocalDateTime.of(2025, 10, 25, 19, 25, 0),
                LocalDateTime.of(2025, 10, 25, 19, 35, 0),
                item,
                booker,
                Status.WAITING);

        // Сериализуем в JSON-формат
        JsonContent<BookingResponseDto> result = json.write(bookingResponseDto);

        String startDate = bookingResponseDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String endDate = bookingResponseDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // Проверяем результат
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(startDate);
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(endDate);
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("ItemName");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(3);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("BookerName");
    }

    private ItemResponseDto createItem() {
        return new ItemResponseDto(
                2L,
                "ItemName",
                "DescriptionName",
                true,
                4L);
    }

    private UserResponseDto createBooker() {
        return new UserResponseDto(3L, "BookerName");
    }
}