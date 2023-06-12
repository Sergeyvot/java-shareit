package ru.practicum.shareit.booking.dao;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface BookingStorage {

    Booking addNewBooking(User user, Item item, BookingDto bookingDto);

    void removeBooking(long id);

    void deleteAllBookings();

    Booking updateBookingByBooker(long bookingId, User booker, Item item, BookingDto bookingDto);

    Collection<BookingDto> getAllBookings();

    BookingDto findBookingById(long id);
}
