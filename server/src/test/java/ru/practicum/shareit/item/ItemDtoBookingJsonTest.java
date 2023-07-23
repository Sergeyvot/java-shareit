package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemDtoBookingJsonTest {

    @Autowired
    private JacksonTester<ItemDtoBooking> json;

    @Test
    void testItemDtoBooking() throws Exception {
        List<CommentDto> listComments = Stream.of(new CommentDto(1L, "Все понравилось", "Author",
                LocalDateTime.of(2023, 7, 12, 12, 30, 0))).collect(Collectors.toList());
        ItemDtoBooking itemDtoBooking = new ItemDtoBooking(
                1L,
                "Отвертка",
                "Крестовая отвертка",
                new BookingDtoItem(1L, LocalDateTime.of(2023, 7, 10, 12, 30, 0),
                        LocalDateTime.of(2023, 7, 11, 12, 30, 0),
                        1L, 1L, "APPROVED"),
                new BookingDtoItem(2L, LocalDateTime.of(2023, 7, 19, 12, 30, 0),
                        LocalDateTime.of(2023, 7, 20, 12, 30, 0),
                        2L, 1L, "WAITING"),
                true, null, listComments);

        JsonContent<ItemDtoBooking> result = json.write(itemDtoBooking);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Отвертка");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Крестовая отвертка");
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.start").isEqualTo("2023-07-10T12:30:00");
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.end").isEqualTo("2023-07-11T12:30:00");
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.status").isEqualTo("APPROVED");
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.start").isEqualTo("2023-07-19T12:30:00");
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.end").isEqualTo("2023-07-20T12:30:00");
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.status").isEqualTo("WAITING");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathValue("$.requestId").isNull();
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("Все понравилось");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo("Author");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created").isEqualTo("2023-07-12T12:30:00");
    }
}
