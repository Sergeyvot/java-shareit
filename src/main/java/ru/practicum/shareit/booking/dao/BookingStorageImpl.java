package ru.practicum.shareit.booking.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
@Slf4j
public class BookingStorageImpl implements BookingStorage {

    @Autowired
    private BookingMapper bookingMapper;
    private final Map<Long, Booking> bookings = new HashMap<>();
    private long id;

    @Override
    public Booking addNewBooking(User user, Item item, BookingDto bookingDto) {
        Booking booking = bookingMapper.toBooking(bookingDto);
        long bookingId = ++id;
        Booking newBooking = booking.toBuilder()
                .bookingId(bookingId)
                .booker(user)
                .item(item)
                .status(Status.WAITING).build();
        bookings.put(bookingId, newBooking);
        log.info("Вещь с Id {} забронирована пользователем с id {}", booking.getItem().getId(),
                booking.getBooker().getId());
        return newBooking;
    }

    @Override
    public void removeBooking(long id) {

    }

    @Override
    public void deleteAllBookings() {

    }

    @Override
    public Booking updateBookingByBooker(long bookingId, User user, BookingDto bookingDto) {
        if (bookings.containsKey(bookingId)) {
            if (bookings.get(bookingId).getBooker().getId() == user.getId() ||
                    bookings.get(bookingId).getItem().getOwner().getId() == user.getId()) {
                Booking booking = bookingMapper.toBooking(bookingDto);
                Booking updateBooking = booking.toBuilder()
                        .bookingId(bookingId)
                        .booker(bookings.get(bookingId).getBooker())
                        .item(bookings.get(bookingId).getItem()).build();
                bookings.put(bookingId, updateBooking);
                log.info("Бронирование веши с Id {} отредактировано пользователем с id {}", booking.getItem().getId(),
                        user.getId());
                return updateBooking;
            } else {
                log.error("Id пользователя {} не совпадает с id автора бронирования {} и id владельца вещи {}.",
                        user.getId(), bookings.get(bookingId).getBooker().getId(),
                        bookings.get(bookingId).getItem().getOwner().getId());
                throw new ValidationException(String.format("Редактировать бронирование c id %d может только его автор "
                                + "с id %d либо владелец вещи с id %d", bookingId,
                        bookings.get(bookingId).getBooker().getId(),
                        bookings.get(bookingId).getItem().getOwner().getId()));
            }
        } else {
            log.error("Передан некорректный id бронирования: {}", bookingId);
            throw new BookingNotFoundException(String.format("Бронирования с id %d не зарегистрировано "
                    + "в базе приложения.", bookingId));
        }
    }

    @Override
    public Collection<BookingDto> getAllBookings() {
        return null;
    }

    @Override
    public BookingDto findBookingById(long id) {
        return null;
    }
}
