package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.ItemStorageImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;

import ru.practicum.shareit.user.dao.UserStorageImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Mapper(uses = {UserMapper.class, ItemMapper.class},
        componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "status", source = "bookingDto.status")
    @Mapping(target = "booker", source = "bookerId", qualifiedByName = "getBookerFromBookerId")
    @Mapping(target = "item", source = "itemId", qualifiedByName = "getItemFromItemId")
    Booking toBooking(BookingDto bookingDto);

    @Mapping(target = "status", source = "booking.status")
    @Mapping(target = "bookerId", source = "booker", qualifiedByName = "getBookerIdFromBooker")
    @Mapping(target = "itemId", source = "item", qualifiedByName = "getItemIdFromItem")
    BookingDto toBookingDto(Booking booking);

    @Named("getBookerFromBookerId")
    default UserDto getBookerFromBookerId(long bookerId) {
        UserStorageImpl userStorage = new UserStorageImpl();

        return userStorage.findUserById(bookerId);
    }

    @Named("getItemFromItemId")
    default ItemDto getItemFromItemId(long itemId) {
        ItemStorageImpl itemStorage = new ItemStorageImpl();
        return itemStorage.findItemById(itemId);
    }

    @Named("getBookerIdFromBooker")
    default Long getBookerIdFromBooker(User booker) {
        return booker.getId();
    }

    @Named("getItemIdFromItem")
    default Long getItemIdFromItem(Item item) {
        return item.getId();
    }
}
