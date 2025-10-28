package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemResponseWithOwnerIdDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestWithItemsDtoJsonTest {

    private final JacksonTester<ItemRequestWithItemsDto> json;

    private static final LocalDateTime CREATED = LocalDateTime
            .of(2025, 10, 25, 19, 25, 0);
    private final String created = CREATED.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    @SneakyThrows
    @Test
    void itemRequestWithItemsDtoSerializationTest() {

        // Создаем объект
        ItemResponseWithOwnerIdDto itemResponseWithOwnerIdDto = new ItemResponseWithOwnerIdDto(
                2L,
                "ItemName",
                3L
        );

        ItemRequestWithItemsDto itemRequestWithItemsDto = new ItemRequestWithItemsDto(
                1L,
                "ItemRequestDescription",
                4L,
                CREATED,
                List.of(itemResponseWithOwnerIdDto)
        );

        // Сериализуем в JSON-формат
        JsonContent<ItemRequestWithItemsDto> result = json.write(itemRequestWithItemsDto);

        // Проверяем результат
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemRequestWithItemsDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemRequestWithItemsDto.getDescription());
        assertThat(result).extractingJsonPathNumberValue("$.requestor").isEqualTo(itemRequestWithItemsDto.getRequestor().intValue());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(created);
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(1);
        assertThat(result).extractingJsonPathNumberValue("$.items.[0].id").isEqualTo(itemResponseWithOwnerIdDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.items.[0].name").isEqualTo(itemResponseWithOwnerIdDto.getName());
        assertThat(result).extractingJsonPathNumberValue("$.items.[0].ownerId").isEqualTo(itemResponseWithOwnerIdDto.getOwnerId().intValue());
    }
}