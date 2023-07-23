package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoItem;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoItemJsonTest {

    @Autowired
    private JacksonTester<BookingDtoItem> json;

    @Test
    void testBookingDtoItem() throws Exception {
        BookingDtoItem bookingDtoItem = new BookingDtoItem(
                1L,
                LocalDateTime.of(2023, 7, 14, 12, 30, 0),
                LocalDateTime.of(2023, 7, 15, 12, 30, 0),
                1L, 1L,"WAITING");

        JsonContent<BookingDtoItem> result = json.write(bookingDtoItem);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-07-14T12:30:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-07-15T12:30:00");
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }
}
