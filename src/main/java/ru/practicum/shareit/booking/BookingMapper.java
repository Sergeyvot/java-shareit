package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(uses = {UserMapper.class, ItemMapper.class, ItemRepository.class},
        componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "status", source = "booking.status")
    @Mapping(target = "itemId", source = "item", qualifiedByName = "getItemIdFromItem")
    @Mapping(target = "start", source = "start", qualifiedByName = "getLocalDateTimeFromInstantStart")
    @Mapping(target = "end", source = "end", qualifiedByName = "getLocalDateTimeFromInstantEnd")
    BookingDto toBookingDto(Booking booking);

    @Named("getItemIdFromItem")
    default Long getItemIdFromItem(Item item) {

        return item.getId();
    }

    @Named("getLocalDateTimeFromInstantStart")
    default LocalDateTime getLocalDateTimeFromInstantStart(Instant start) {
        return LocalDateTime.ofInstant(start, ZoneId.systemDefault());
    }

    @Named("getLocalDateTimeFromInstantEnd")
    default LocalDateTime getLocalDateTimeFromInstantEnd(Instant end) {
        return LocalDateTime.ofInstant(end, ZoneId.systemDefault());
    }
}
