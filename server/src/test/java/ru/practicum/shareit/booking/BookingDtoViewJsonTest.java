package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoView;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoViewJsonTest {

    @Autowired
    private JacksonTester<BookingDtoView> json;

    @Test
    void testBookingDtoView() throws Exception {
        BookingDtoView bookingDtoView = new BookingDtoView(
                1L,
                LocalDateTime.of(2023, 7, 14, 12, 30, 0),
                LocalDateTime.of(2023, 7, 15, 12, 30, 0),
                new User(1L, "Booker", "booker@mail.ru"),
                new Item(1L, "Отвертка", "Крестовая отвертка", true,
                        new User(2L, "Owner", "owner@mail.ru"), null),"WAITING");

        JsonContent<BookingDtoView> result = json.write(bookingDtoView);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-07-14T12:30:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-07-15T12:30:00");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("Booker");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("booker@mail.ru");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Отвертка");
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo("Крестовая отвертка");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.item.owner.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.item.owner.name").isEqualTo("Owner");
        assertThat(result).extractingJsonPathStringValue("$.item.owner.email").isEqualTo("owner@mail.ru");
        assertThat(result).extractingJsonPathValue("$.item.requestId").isNull();
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }
}
