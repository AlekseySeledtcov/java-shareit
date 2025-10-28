package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentDtoJsonTest {

    private final JacksonTester<CommentDto> json;
    private final ObjectMapper objectMapper;

    private static final LocalDateTime CREATED = LocalDateTime
            .of(2025, 10, 25, 19, 25, 0);
    private final String created = CREATED.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    @SneakyThrows
    @Test
    void commentDtoJsonDeserializationTest() {

        // Создаем Json строку
        Map<String, Object> jsonMap = Map.of(
                "text", "CommentText",
                "authorName", "CommentAuthor",
                "created", created
        );

        String jsonString = objectMapper.writeValueAsString(jsonMap);

        // Десериализуем
        CommentDto deserialized = json.parseObject(jsonString);

        // Проверяем результат
        assertThat(deserialized.getText()).isEqualTo("CommentText");
        assertThat(deserialized.getAuthorName()).isEqualTo("CommentAuthor");
        assertThat(deserialized.getCreated()).isEqualTo(CREATED);
    }


    @SneakyThrows
    @Test
    void commentDtoSerializationTest() {
        // Создаем объект
        CommentDto commentDto = new CommentDto(1L, "CommentName", "AuthorName", CREATED);

        // Сериализуем в JSON-формат
        JsonContent<CommentDto> result = json.write(commentDto);

        // Проверяем результат
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(commentDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(commentDto.getText());
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo(commentDto.getAuthorName());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(created);
    }
}