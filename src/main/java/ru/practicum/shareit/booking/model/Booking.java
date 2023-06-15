package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Value
@Builder(toBuilder = true)
public class Booking {
    long bookingId;
    LocalDateTime start;
    LocalDateTime end;
    Item item;
    User booker;
    Status status;
}
