package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dao.ItemRepository;

import java.time.OffsetDateTime;

@Component
public class BookingDtoMapper {
    private final ItemRepository repository;

    @Autowired
    public BookingDtoMapper(ItemRepository repository) {
        this.repository = repository;
    }

    public Booking toBooking(BookingDto bookingDto) {
        Booking.BookingBuilder booking = Booking.builder();

        if (bookingDto.getId() != null) {
            booking.id(bookingDto.getId());
        }
        booking.start(bookingDto.getStart().toInstant(OffsetDateTime.now().getOffset()));
        booking.end(bookingDto.getEnd().toInstant(OffsetDateTime.now().getOffset()));
        if (bookingDto.getStatus() != null) {
            booking.status(Status.valueOf(bookingDto.getStatus()));
        }
        booking.item(repository.findById(bookingDto.getItemId()).get());

        return booking.build();
    }

}
