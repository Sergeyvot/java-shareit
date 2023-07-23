package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoView;

import java.util.List;

public interface BookingService {

    BookingDtoView addNewBooking(Long userId, BookingDto bookingDto);

    BookingDtoView updateApproved(Long userId,Long bookingId, Boolean approved);

    BookingDtoView getBookingById(Long bookingId, Long userId);

    List<BookingDtoView> getAllBookingsByBookerId(Long userId, String state, Integer from, Integer size);

    List<BookingDtoView> getAllBookingsByOwnerId(Long userId, String state, Integer from, Integer size);
}
