package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoView;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTest {

    private final BookingService service;
    private final ItemService itemService;
    private final UserService userService;

    @Test
    void testGetAllBookingsByBookerId() {
        UserDto userDto1 = makeUserDto("some@email.com", "UserName");
        UserDto owner = userService.createUser(userDto1);
        UserDto userDto2 = makeUserDto("some@yandex.ru", "Vasilii");
        UserDto booker = userService.createUser(userDto2);

        ItemDto itemDto1 = itemService.addNewItem(owner.getId(),
                makeItemDto("Отвертка", "Крестовая отвертка", true));
        ItemDto itemDto2 = itemService.addNewItem(owner.getId(),
                makeItemDto("Чайник", "Новый чайник", true));
        ItemDto itemDto3 = itemService.addNewItem(owner.getId(),
                makeItemDto("Дрель", "Аккумуляторная дрель", true));

        List<BookingDto> sourceBookings = Stream.of(
                makeBookingDto(LocalDateTime.of(2023, 7, 24, 13, 0),
                        LocalDateTime.of(2023, 7, 25, 13, 0), itemDto1.getId()),
                makeBookingDto(LocalDateTime.of(2023, 7, 21, 13, 0),
                        LocalDateTime.of(2023, 7, 23, 13, 0), itemDto2.getId()),
                makeBookingDto(LocalDateTime.of(2023, 7, 20, 13, 0),
                        LocalDateTime.of(2023, 7, 22, 13, 0), itemDto3.getId())
        ).collect(Collectors.toList());

        for (BookingDto booking : sourceBookings) {
            service.addNewBooking(booker.getId(), booking);
        }
        int i = 0;
        List<BookingDtoView> targetBookings = service.getAllBookingsByBookerId(booker.getId(), "ALL", 0, 5);

        assertThat(targetBookings, hasSize(sourceBookings.size()));
        for (BookingDtoView targetBooking : targetBookings) {
            assertThat(targetBooking.getId(), notNullValue());
            assertThat(targetBooking.getStart(), equalTo(sourceBookings.get(i).getStart()));
            assertThat(targetBooking.getEnd(), equalTo(sourceBookings.get(i).getEnd()));
            assertThat(targetBooking.getBooker().getId(), equalTo(booker.getId()));
            assertThat(targetBooking.getItem(), notNullValue());
            assertThat(targetBooking.getStatus(), equalTo("WAITING"));
            i++;
        }
    }

    private UserDto makeUserDto(String email, String name) {
        return UserDto.builder()
                .name(name)
                .email(email).build();
    }

    private ItemDto makeItemDto(String name, String description, Boolean available) {
        return ItemDto.builder()
                .name(name)
                .description(description)
                .available(available).build();
    }

    private BookingDto makeBookingDto(LocalDateTime start, LocalDateTime end, long itemId) {
        return BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(itemId).build();
    }
}
