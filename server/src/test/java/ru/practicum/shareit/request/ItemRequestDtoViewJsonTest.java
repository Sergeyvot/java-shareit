package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoView;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemRequestDtoViewJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDtoView> json;

    @Test
    void testItemRequestDto() throws Exception {
        ItemRequestDtoView requestDtoView = new ItemRequestDtoView(
                1L,
                "Нужна отвертка",
                LocalDateTime.of(2023, 7, 15, 12, 30, 0),
                Stream.of(new ItemDto(1L, "Отвертка", "Крестовая отвертка", true, 1L))
                        .collect(Collectors.toList()));

        JsonContent<ItemRequestDtoView> result = json.write(requestDtoView);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Нужна отвертка");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2023-07-15T12:30:00");
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Отвертка");
        assertThat(result).extractingJsonPathStringValue("$.items[0].description").isEqualTo("Крестовая отвертка");
        assertThat(result).extractingJsonPathBooleanValue("$.items[0].available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].requestId").isEqualTo(1);
    }
}
