package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemResponseWithBookingDateDtoJsonTest {

    private final JacksonTester<ItemResponseWithBookingDateDto> json;

    private static final  LocalDateTime CREATED = LocalDateTime
            .of(2025, 10, 25, 19, 25, 0);

    @SneakyThrows
    @Test
    void itemResponseWithBookingDateDtoSerializationTest() {
        // Создаем объект
        CommentDto commentDto = new CommentDto(1L, "CommentName", "AuthorName", CREATED);
        List<CommentDto> comments = List.of(commentDto);

        ItemResponseWithBookingDateDto itemResponse = new ItemResponseWithBookingDateDto(
                1L,
                "ItemName",
                "ItemDescription",
                true,
                CREATED,
                CREATED.plusMinutes(10),
                comments
        );

        // Сериализуем в JSON-формат
        JsonContent<ItemResponseWithBookingDateDto> result = json.write(itemResponse);

        // Проверяем результат
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemResponse.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemResponse.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemResponse.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemResponse.isAvailable());
        assertThat(result).extractingJsonPathStringValue("$.lastBooking").isEqualTo(itemResponse.getLastBooking()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.nextBooking").isEqualTo(itemResponse.getNextBooking()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathArrayValue("$.comments").hasSize(comments.size());
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(comments.getFirst().getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo(comments.getFirst().getText());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo(comments.getFirst().getAuthorName());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created")
                .isEqualTo(comments.getFirst().getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}