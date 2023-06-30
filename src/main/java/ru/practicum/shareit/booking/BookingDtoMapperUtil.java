package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.time.OffsetDateTime;

@Component
public final class BookingDtoMapperUtil {

    private BookingDtoMapperUtil() {
    }    

    public static Booking toBooking(BookingDto bookingDto, Item item) {
        Booking.BookingBuilder booking = Booking.builder();

        if (bookingDto.getId() != null) {
            booking.id(bookingDto.getId());
        }
        booking.start(bookingDto.getStart().toInstant(OffsetDateTime.now().getOffset()));
        booking.end(bookingDto.getEnd().toInstant(OffsetDateTime.now().getOffset()));
        if (bookingDto.getStatus() != null) {
            booking.status(Status.valueOf(bookingDto.getStatus()));
        }
        booking.item(item);

        return booking.build();
    }
}
