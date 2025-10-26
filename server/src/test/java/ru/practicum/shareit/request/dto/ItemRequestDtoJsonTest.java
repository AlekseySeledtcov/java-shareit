package ru.practicum.shareit.request.dto;

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
class ItemRequestDtoJsonTest {

    private final JacksonTester<ItemRequestDto> json;
    private final ObjectMapper objectMapper;

    private static final LocalDateTime CREATED = LocalDateTime
            .of(2025, 10, 25, 19, 25, 0);
    private final String created = CREATED.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    @SneakyThrows
    @Test
    void itemRequestDtoDeserializationTest() {

        // Создаем Json строку
        Map<String, Object> jsonMap = Map.of(
                "id", 1,
                "description", "ItemDescription",
                "requestor", 2,
                "created", created
        );

        String jsonString = objectMapper.writeValueAsString(jsonMap);

        // Десериализуем
        ItemRequestDto deserialized = json.parseObject(jsonString);

        // Проверяем результат
        assertThat(deserialized.getId()).isNull();
        assertThat(deserialized.getDescription()).isEqualTo("ItemDescription");
        assertThat(deserialized.getRequestor()).isEqualTo(2L);
        assertThat(deserialized.getCreated()).isEqualTo(CREATED);
    }

    @SneakyThrows
    @Test
    void itemRequestDtoSerializationTest() {
        // Создаем объект
        ItemRequestDto itemRequestDto = new ItemRequestDto(
                1L,
                "ItemDescription",
                2L,
                CREATED
        );

        // Сериализуем в JSON-формат
        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        // Проверяем результат
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemRequestDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemRequestDto.getDescription());
        assertThat(result).extractingJsonPathNumberValue("$.requestor").isEqualTo(itemRequestDto.getRequestor().intValue());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(created);
    }
}