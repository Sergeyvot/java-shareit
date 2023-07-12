package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoView;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ParameterStateException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    BookingRepository mockBookingRepository;
    @Mock
    ItemService mockItemService;
    @Mock
    UserService mockUserService;
    @Autowired
    UserMapper userMapper;
    @Autowired
    BookingDtoViewMapper bookingDtoViewMapper;
    BookingService bookingService;

    @BeforeEach
    @Test
    void initializingService() {
        bookingService = new BookingServiceImpl(mockBookingRepository, mockItemService, mockUserService,
                userMapper, bookingDtoViewMapper);
    }

    @Test
    void testAddNewBooking() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(1L, "NameTest", "test@mail.ru"));
        Mockito
                .when(mockItemService.findEntityById(Mockito.anyLong()))
                .thenReturn(new Item(1L, "Отвертка", "Крестовая отвертка", true,
                        new User(2L, "Owner", "owner@mail.ru"), null));
        Mockito
                .when(mockBookingRepository.save(Mockito.any()))
                .thenReturn(new Booking(1L, Instant.now().plusSeconds(1000),
                        Instant.now().plusSeconds(2000),
                        new Item(1L, "Отвертка", "Крестовая отвертка", true,
                                new User(2L, "Owner", "owner@mail.ru"), null),
                        new User(1L, "NameTest", "test@mail.ru"), Status.WAITING));

        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusSeconds(1000))
                .end(LocalDateTime.now().plusSeconds(2000))
                .itemId(1L).build();

        BookingDtoView newBooking = bookingService.addNewBooking(1L, bookingDto);
        Assertions.assertEquals(1L, newBooking.getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Отвертка", newBooking.getItem().getName(), "Поля объектов не совпадают");
        Assertions.assertEquals("owner@mail.ru", newBooking.getItem().getOwner().getEmail(), "Поля объектов не совпадают");
        Assertions.assertEquals("NameTest", newBooking.getBooker().getName(), "Поля объектов не совпадают");
        Assertions.assertEquals("WAITING", newBooking.getStatus(), "Поля объектов не совпадают");
    }

    @Test
    void testAddNewBookingWhenStartDateTimeNull() {
        BookingDto bookingDto = BookingDto.builder()
                .end(LocalDateTime.now().plusSeconds(2000))
                .itemId(1L).build();

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.addNewBooking(1L, bookingDto));

        Assertions.assertEquals("Время бронирования задано некорректно.", exception.getMessage());
    }

    @Test
    void testAddNewBookingWhenEndDateTimeNull() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusSeconds(1000))
                .itemId(1L).build();

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.addNewBooking(1L, bookingDto));

        Assertions.assertEquals("Время бронирования задано некорректно.", exception.getMessage());
    }

    @Test
    void testAddNewBookingWhenEndDateTimeIsBeforeStartDateTime() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusSeconds(2000))
                .end(LocalDateTime.now().plusSeconds(1000))
                .itemId(1L).build();

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.addNewBooking(1L, bookingDto));

        Assertions.assertEquals("Время бронирования задано некорректно.", exception.getMessage());
    }

    @Test
    void testAddNewBookingWhenEndDateTimeEqualToStartDateTime() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusSeconds(1000))
                .end(LocalDateTime.now().plusSeconds(1000))
                .itemId(1L).build();

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.addNewBooking(1L, bookingDto));

        Assertions.assertEquals("Время бронирования задано некорректно.", exception.getMessage());
    }

    @Test
    void testAddNewBookingWhenStartDateTimeInPast() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().minusSeconds(1000))
                .end(LocalDateTime.now().plusSeconds(1000))
                .itemId(1L).build();

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.addNewBooking(1L, bookingDto));

        Assertions.assertEquals("Время бронирования задано некорректно.", exception.getMessage());
    }

    @Test
    void testAddNewBookingWhenOwnerIdEqualToBookerId() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(1L, "NameTest", "test@mail.ru"));
        Mockito
                .when(mockItemService.findEntityById(Mockito.anyLong()))
                .thenReturn(new Item(1L, "Отвертка", "Крестовая отвертка", true,
                        new User(1L, "Owner", "owner@mail.ru"), null));

        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusSeconds(1000))
                .end(LocalDateTime.now().plusSeconds(2000))
                .itemId(1L).build();

        final UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> bookingService.addNewBooking(1L, bookingDto));

        Assertions.assertEquals("Владелец вещи не должен быть автором бронирования", exception.getMessage());
    }

    @Test
    void testAddNewBookingWhenItemNotAvailable() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(1L, "NameTest", "test@mail.ru"));
        Mockito
                .when(mockItemService.findEntityById(Mockito.anyLong()))
                .thenReturn(new Item(1L, "Отвертка", "Крестовая отвертка", false,
                        new User(2L, "Owner", "owner@mail.ru"), null));

        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusSeconds(1000))
                .end(LocalDateTime.now().plusSeconds(2000))
                .itemId(1L).build();

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.addNewBooking(1L, bookingDto));

        Assertions.assertEquals("Вещь с id 1 не доступна для бронирования", exception.getMessage());
    }

    @Test
    void testUpdateApproved() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(2L, "Owner", "owner@mail.ru"));
        Mockito
                .when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Booking(1L, Instant.now().plusSeconds(1000),
                        Instant.now().plusSeconds(2000),
                        new Item(1L, "Отвертка", "Крестовая отвертка", true,
                                new User(2L, "Owner", "owner@mail.ru"), null),
                        new User(1L, "NameTest", "test@mail.ru"), Status.WAITING)));
        Mockito
                .when(mockBookingRepository.save(Mockito.any()))
                .thenReturn(new Booking(1L, Instant.now().plusSeconds(1000),
                        Instant.now().plusSeconds(2000),
                        new Item(1L, "Отвертка", "Крестовая отвертка", true,
                                new User(2L, "Owner", "owner@mail.ru"), null),
                        new User(1L, "NameTest", "test@mail.ru"), Status.APPROVED));

        BookingDtoView updateBooking = bookingService.updateApproved(2L, 1L, true);
        Assertions.assertEquals(1L, updateBooking.getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Отвертка", updateBooking.getItem().getName(), "Поля объектов не совпадают");
        Assertions.assertEquals("owner@mail.ru", updateBooking.getItem().getOwner().getEmail(), "Поля объектов не совпадают");
        Assertions.assertEquals("NameTest", updateBooking.getBooker().getName(), "Поля объектов не совпадают");
        Assertions.assertEquals("APPROVED", updateBooking.getStatus(), "Поля объектов не совпадают");
    }

    @Test
    void testUpdateApprovedWithIncorrectUserId() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenThrow(new UserNotFoundException("Пользователь с id 9999 не зарегистрирован "
                        + "в базе приложения."));

        final UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> bookingService.updateApproved(9999L, 1L, true));

        Assertions.assertEquals("Пользователь с id 9999 не зарегистрирован в базе приложения.",
                exception.getMessage());
    }

    @Test
    void testUpdateApprovedWithIncorrectBookingId() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(2L, "Owner", "owner@mail.ru"));
        Mockito
                .when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenThrow(new BookingNotFoundException("Бронирование с id 9999 не зарегистрировано "
                        + "в базе приложения."));

        final BookingNotFoundException exception = Assertions.assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.updateApproved(2L, 9999L, true));

        Assertions.assertEquals("Бронирование с id 9999 не зарегистрировано в базе приложения.",
                exception.getMessage());
    }

    @Test
    void testUpdateApprovedWhenBookingStatusApproved() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(2L, "Owner", "owner@mail.ru"));
        Mockito
                .when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Booking(1L, Instant.now().plusSeconds(1000),
                        Instant.now().plusSeconds(2000),
                        new Item(1L, "Отвертка", "Крестовая отвертка", true,
                                new User(2L, "Owner", "owner@mail.ru"), null),
                        new User(1L, "NameTest", "test@mail.ru"), Status.APPROVED)));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.updateApproved(2L, 1L, true));

        Assertions.assertEquals("Бронирование вещи с id 1 уже подтверждено", exception.getMessage());
    }

    @Test
    void testUpdateApprovedWithIncorrectOwnerId() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(2L, "OtherUser", "other@mail.ru"));
        Mockito
                .when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Booking(1L, Instant.now().plusSeconds(1000),
                        Instant.now().plusSeconds(2000),
                        new Item(1L, "Отвертка", "Крестовая отвертка", true,
                                new User(3L, "Owner", "owner@mail.ru"), null),
                        new User(1L, "NameTest", "test@mail.ru"), Status.WAITING)));

        final UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> bookingService.updateApproved(2L, 1L, true));

        Assertions.assertEquals("Пользователь с id 2 не является владельцем вещи",
                exception.getMessage());
    }

    @Test
    void testGetBookingByIdWhenRequesterIsOwner() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(2L, "Owner", "owner@mail.ru"));
        Mockito
                .when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Booking(1L, Instant.now().plusSeconds(1000),
                        Instant.now().plusSeconds(2000),
                        new Item(1L, "Отвертка", "Крестовая отвертка", true,
                                new User(2L, "Owner", "owner@mail.ru"), null),
                        new User(1L, "NameTest", "test@mail.ru"), Status.WAITING)));

        BookingDtoView bookingDtoView = bookingService.getBookingById(1L, 2L);
        Assertions.assertEquals(1L, bookingDtoView.getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Отвертка", bookingDtoView.getItem().getName(), "Поля объектов не совпадают");
        Assertions.assertEquals("owner@mail.ru", bookingDtoView.getItem().getOwner().getEmail(), "Поля объектов не совпадают");
        Assertions.assertEquals("NameTest", bookingDtoView.getBooker().getName(), "Поля объектов не совпадают");
        Assertions.assertEquals("WAITING", bookingDtoView.getStatus(), "Поля объектов не совпадают");
    }

    @Test
    void testGetBookingByIdWhenRequesterIsBooker() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(2L, "Owner", "owner@mail.ru"));
        Mockito
                .when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Booking(1L, Instant.now().plusSeconds(1000),
                        Instant.now().plusSeconds(2000),
                        new Item(1L, "Отвертка", "Крестовая отвертка", true,
                                new User(2L, "Owner", "owner@mail.ru"), null),
                        new User(1L, "NameTest", "test@mail.ru"), Status.WAITING)));

        BookingDtoView bookingDtoView = bookingService.getBookingById(1L, 1L);
        Assertions.assertEquals(1L, bookingDtoView.getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Отвертка", bookingDtoView.getItem().getName(), "Поля объектов не совпадают");
        Assertions.assertEquals("owner@mail.ru", bookingDtoView.getItem().getOwner().getEmail(), "Поля объектов не совпадают");
        Assertions.assertEquals("NameTest", bookingDtoView.getBooker().getName(), "Поля объектов не совпадают");
        Assertions.assertEquals("WAITING", bookingDtoView.getStatus(), "Поля объектов не совпадают");
    }

    @Test
    void testGetBookingByIdWithIncorrectRequesterId() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(3L, "Owner", "owner@mail.ru"));
        Mockito
                .when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Booking(1L, Instant.now().plusSeconds(1000),
                        Instant.now().plusSeconds(2000),
                        new Item(1L, "Отвертка", "Крестовая отвертка", true,
                                new User(2L, "Owner", "owner@mail.ru"), null),
                        new User(1L, "NameTest", "test@mail.ru"), Status.WAITING)));

        final UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> bookingService.getBookingById(1L, 3L));

        Assertions.assertEquals("Пользователь с id 3 не является автором бронирования "
                + "либо владельцем вещи", exception.getMessage());
    }

    @Test
    void testGetBookingByIdWithIncorrectUserId() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenThrow(new UserNotFoundException("Пользователь с id 9999 не зарегистрирован "
                        + "в базе приложения."));

        final UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> bookingService.getBookingById(1L, 9999L));

        Assertions.assertEquals("Пользователь с id 9999 не зарегистрирован в базе приложения.",
                exception.getMessage());
    }

    @Test
    void testGetBookingByIdWithIncorrectBookingId() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(3L, "Owner", "owner@mail.ru"));
        Mockito
                .when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenThrow(new BookingNotFoundException("Бронирование с id 9999 не зарегистрировано "
                        + "в базе приложения."));

        final BookingNotFoundException exception = Assertions.assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.getBookingById(9999L, 3L));

        Assertions.assertEquals("Бронирование с id 9999 не зарегистрировано в базе приложения.",
                exception.getMessage());
    }

    @Test
    void testGetAllBookingsByBookerIdWhenStateIsWAITING() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Booking> list = Stream.of(new Booking(1L, Instant.now().plusSeconds(1000),
                        Instant.now().plusSeconds(2000),
                        new Item(1L, "Отвертка", "Крестовая отвертка", true,
                                new User(2L, "Owner", "owner@mail.ru"), null),
                        new User(1L, "NameTest", "test@mail.ru"), Status.WAITING),
                new Booking(2L, Instant.now().plusSeconds(3000),
                        Instant.now().plusSeconds(4000),
                        new Item(2L, "Чайник", "Новый чайник", true,
                                new User(3L, "OtherOwner", "test@mail.ru"), null),
                        new User(1L, "NameTest", "test@mail.ru"), Status.WAITING)).collect(Collectors.toList());
        Page<Booking> page =
                new PageImpl<>(list, pageable, list.size());
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(1L, "NameTest", "test@mail.ru"));
        Mockito
                .when(mockBookingRepository.findByBookerIdAndByStatus(Mockito.anyLong(), Mockito.anyString(), Mockito.any()))
                .thenReturn(page);

        List<BookingDtoView> resultList = bookingService.getAllBookingsByBookerId(1L, "WAITING", 0, 5);
        Assertions.assertEquals(2, resultList.size(), "Размер списка не совпадает");
        Assertions.assertEquals(1L, resultList.get(0).getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Отвертка", resultList.get(0).getItem().getName(),
                "Поля объектов не совпадают");
        Assertions.assertEquals("Owner", resultList.get(0).getItem().getOwner().getName(),
                "Поля объектов не совпадают");
        Assertions.assertTrue(resultList.get(0).getItem().isAvailable(), "Поля объектов не совпадают");
        Assertions.assertNull(resultList.get(1).getItem().getRequest(), "Поля объектов не совпадают");
        Assertions.assertEquals(2L, resultList.get(1).getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Чайник", resultList.get(1).getItem().getName(), "Поля объектов не совпадают");
        Assertions.assertEquals("Новый чайник", resultList.get(1).getItem().getDescription(),
                "Поля объектов не совпадают");
    }

    @Test
    void testGetAllBookingsByBookerIdWithIncorrectUserId() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenThrow(new UserNotFoundException("Пользователь с id 9999 не зарегистрирован "
                        + "в базе приложения."));

        final UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> bookingService.getAllBookingsByBookerId(9999L, "WAITING", 0, 5));

        Assertions.assertEquals("Пользователь с id 9999 не зарегистрирован в базе приложения.",
                exception.getMessage());
    }

    @Test
    void testGetAllBookingsByBookerIdWithIncorrectParameterFrom() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(1L, "NameTest", "test@mail.ru"));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.getAllBookingsByBookerId(1L, "WAITING", -5, 5));

        Assertions.assertEquals("Переданы некорректные параметры постраничного вывода", exception.getMessage());
    }

    @Test
    void testGetAllBookingsByBookerIdWithIncorrectParameterSize() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(1L, "NameTest", "test@mail.ru"));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.getAllBookingsByBookerId(1L, "WAITING", 0, -5));

        Assertions.assertEquals("Переданы некорректные параметры постраничного вывода", exception.getMessage());
    }

    @Test
    void testGetAllBookingsByBookerIdWhenFromEqualTo0AndSizeEqualTo0() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(1L, "NameTest", "test@mail.ru"));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.getAllBookingsByBookerId(1L, "WAITING", 0, 0));

        Assertions.assertEquals("Переданы некорректные параметры постраничного вывода", exception.getMessage());
    }

    @Test
    void testGetAllBookingsByBookerIdWhenStateIsREJECTED() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Booking> list = Stream.of(new Booking(1L, Instant.now().plusSeconds(1000),
                        Instant.now().plusSeconds(2000),
                        new Item(1L, "Отвертка", "Крестовая отвертка", true,
                                new User(2L, "Owner", "owner@mail.ru"), null),
                        new User(1L, "NameTest", "test@mail.ru"), Status.REJECTED),
                new Booking(2L, Instant.now().plusSeconds(3000),
                        Instant.now().plusSeconds(4000),
                        new Item(2L, "Чайник", "Новый чайник", true,
                                new User(3L, "OtherOwner", "test@mail.ru"), null),
                        new User(1L, "NameTest", "test@mail.ru"), Status.REJECTED)).collect(Collectors.toList());
        Page<Booking> page =
                new PageImpl<>(list, pageable, list.size());
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(1L, "NameTest", "test@mail.ru"));
        Mockito
                .when(mockBookingRepository.findByBookerIdAndByStatus(Mockito.anyLong(), Mockito.anyString(), Mockito.any()))
                .thenReturn(page);

        List<BookingDtoView> resultList = bookingService.getAllBookingsByBookerId(1L, "REJECTED", 0, 5);
        Assertions.assertEquals(2, resultList.size(), "Размер списка не совпадает");
        Assertions.assertEquals(1L, resultList.get(0).getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Отвертка", resultList.get(0).getItem().getName(),
                "Поля объектов не совпадают");
        Assertions.assertEquals("REJECTED", resultList.get(0).getStatus(),
                "Поля объектов не совпадают");
        Assertions.assertTrue(resultList.get(0).getItem().isAvailable(), "Поля объектов не совпадают");
        Assertions.assertNull(resultList.get(1).getItem().getRequest(), "Поля объектов не совпадают");
        Assertions.assertEquals(2L, resultList.get(1).getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Чайник", resultList.get(1).getItem().getName(), "Поля объектов не совпадают");
        Assertions.assertEquals("REJECTED", resultList.get(1).getStatus(),
                "Поля объектов не совпадают");
    }

    @Test
    void testGetAllBookingsByBookerIdWhenStateIsALL() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Booking> list = Stream.of(new Booking(1L, Instant.now().plusSeconds(1000),
                        Instant.now().plusSeconds(2000),
                        new Item(1L, "Отвертка", "Крестовая отвертка", true,
                                new User(2L, "Owner", "owner@mail.ru"), null),
                        new User(1L, "NameTest", "test@mail.ru"), Status.WAITING),
                new Booking(2L, Instant.now().plusSeconds(3000),
                        Instant.now().plusSeconds(4000),
                        new Item(2L, "Чайник", "Новый чайник", true,
                                new User(3L, "OtherOwner", "test@mail.ru"), null),
                        new User(1L, "NameTest", "test@mail.ru"), Status.APPROVED)).collect(Collectors.toList());
        Page<Booking> page =
                new PageImpl<>(list, pageable, list.size());
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(1L, "NameTest", "test@mail.ru"));
        Mockito
                .when(mockBookingRepository.findByBookerId(Mockito.anyLong(), Mockito.any()))
                .thenReturn(page);

        List<BookingDtoView> resultList = bookingService.getAllBookingsByBookerId(1L, "ALL", 0, 5);
        Assertions.assertEquals(2, resultList.size(), "Размер списка не совпадает");
        Assertions.assertEquals(1L, resultList.get(0).getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Отвертка", resultList.get(0).getItem().getName(),
                "Поля объектов не совпадают");
        Assertions.assertEquals("WAITING", resultList.get(0).getStatus(),
                "Поля объектов не совпадают");
        Assertions.assertTrue(resultList.get(0).getItem().isAvailable(), "Поля объектов не совпадают");
        Assertions.assertNull(resultList.get(1).getItem().getRequest(), "Поля объектов не совпадают");
        Assertions.assertEquals(2L, resultList.get(1).getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Чайник", resultList.get(1).getItem().getName(), "Поля объектов не совпадают");
        Assertions.assertEquals("APPROVED", resultList.get(1).getStatus(),
                "Поля объектов не совпадают");
    }

    @Test
    void testGetAllBookingsByBookerIdWhenStateIsCURRENT() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Booking> list = Stream.of(new Booking(1L, Instant.now().minusSeconds(1000),
                        Instant.now().plusSeconds(2000),
                        new Item(1L, "Отвертка", "Крестовая отвертка", true,
                                new User(2L, "Owner", "owner@mail.ru"), null),
                        new User(1L, "NameTest", "test@mail.ru"), Status.WAITING),
                new Booking(2L, Instant.now().minusSeconds(3000),
                        Instant.now().plusSeconds(4000),
                        new Item(2L, "Чайник", "Новый чайник", true,
                                new User(3L, "OtherOwner", "test@mail.ru"), null),
                        new User(1L, "NameTest", "test@mail.ru"), Status.APPROVED)).collect(Collectors.toList());
        Page<Booking> page =
                new PageImpl<>(list, pageable, list.size());
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(1L, "NameTest", "test@mail.ru"));
        Mockito
                .when(mockBookingRepository.findByBookerId(Mockito.anyLong(), Mockito.any()))
                .thenReturn(page);

        List<BookingDtoView> resultList = bookingService.getAllBookingsByBookerId(1L, "CURRENT", 0, 5);
        Assertions.assertEquals(2, resultList.size(), "Размер списка не совпадает");
        Assertions.assertEquals(1L, resultList.get(0).getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Отвертка", resultList.get(0).getItem().getName(),
                "Поля объектов не совпадают");
        Assertions.assertEquals("WAITING", resultList.get(0).getStatus(),
                "Поля объектов не совпадают");
        Assertions.assertTrue(resultList.get(0).getItem().isAvailable(), "Поля объектов не совпадают");
        Assertions.assertNull(resultList.get(1).getItem().getRequest(), "Поля объектов не совпадают");
        Assertions.assertEquals(2L, resultList.get(1).getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Чайник", resultList.get(1).getItem().getName(), "Поля объектов не совпадают");
        Assertions.assertEquals("APPROVED", resultList.get(1).getStatus(),
                "Поля объектов не совпадают");
    }

    @Test
    void testGetAllBookingsByBookerIdWhenStateIsFUTURE() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Booking> list = Stream.of(new Booking(1L, Instant.now().plusSeconds(1000),
                        Instant.now().plusSeconds(2000),
                        new Item(1L, "Отвертка", "Крестовая отвертка", true,
                                new User(2L, "Owner", "owner@mail.ru"), null),
                        new User(1L, "NameTest", "test@mail.ru"), Status.WAITING),
                new Booking(2L, Instant.now().minusSeconds(3000),
                        Instant.now().plusSeconds(4000),
                        new Item(2L, "Чайник", "Новый чайник", true,
                                new User(3L, "OtherOwner", "test@mail.ru"), null),
                        new User(1L, "NameTest", "test@mail.ru"), Status.APPROVED)).collect(Collectors.toList());
        Page<Booking> page =
                new PageImpl<>(list, pageable, list.size());
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(1L, "NameTest", "test@mail.ru"));
        Mockito
                .when(mockBookingRepository.findByBookerId(Mockito.anyLong(), Mockito.any()))
                .thenReturn(page);

        List<BookingDtoView> resultList = bookingService.getAllBookingsByBookerId(1L, "FUTURE", 0, 5);
        Assertions.assertEquals(1, resultList.size(), "Размер списка не совпадает");
        Assertions.assertEquals(1L, resultList.get(0).getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Отвертка", resultList.get(0).getItem().getName(),
                "Поля объектов не совпадают");
        Assertions.assertEquals("WAITING", resultList.get(0).getStatus(),
                "Поля объектов не совпадают");
        Assertions.assertTrue(resultList.get(0).getItem().isAvailable(), "Поля объектов не совпадают");
    }

    @Test
    void testGetAllBookingsByBookerIdWhenStateIsPAST() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Booking> list = Stream.of(new Booking(1L, Instant.now().plusSeconds(1000),
                        Instant.now().plusSeconds(2000),
                        new Item(1L, "Отвертка", "Крестовая отвертка", true,
                                new User(2L, "Owner", "owner@mail.ru"), null),
                        new User(1L, "NameTest", "test@mail.ru"), Status.WAITING),
                new Booking(2L, Instant.now().minusSeconds(4000),
                        Instant.now().minusSeconds(3000),
                        new Item(2L, "Чайник", "Новый чайник", true,
                                new User(3L, "OtherOwner", "test@mail.ru"), null),
                        new User(1L, "NameTest", "test@mail.ru"), Status.APPROVED)).collect(Collectors.toList());
        Page<Booking> page =
                new PageImpl<>(list, pageable, list.size());
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(1L, "NameTest", "test@mail.ru"));
        Mockito
                .when(mockBookingRepository.findByBookerId(Mockito.anyLong(), Mockito.any()))
                .thenReturn(page);

        List<BookingDtoView> resultList = bookingService.getAllBookingsByBookerId(1L, "PAST", 0, 5);
        Assertions.assertEquals(1, resultList.size(), "Размер списка не совпадает");
        Assertions.assertEquals(2L, resultList.get(0).getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Чайник", resultList.get(0).getItem().getName(),
                "Поля объектов не совпадают");
        Assertions.assertEquals("APPROVED", resultList.get(0).getStatus(),
                "Поля объектов не совпадают");
        Assertions.assertTrue(resultList.get(0).getItem().isAvailable(), "Поля объектов не совпадают");
    }

    @Test
    void testGetAllBookingsByBookerIdWhenStateIncorrect() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(1L, "NameTest", "test@mail.ru"));

        final ParameterStateException exception = Assertions.assertThrows(
                ParameterStateException.class,
                () -> bookingService.getAllBookingsByBookerId(1L, "SOMETHING", 0, 5));

        Assertions.assertEquals("Unknown state: SOMETHING", exception.getMessage());
    }

    @Test
    void testGetAllBookingsByOwnerIdWhenStateIsWAITING() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Booking> list = Stream.of(new Booking(1L, Instant.now().plusSeconds(1000),
                        Instant.now().plusSeconds(2000),
                        new Item(1L, "Отвертка", "Крестовая отвертка", true,
                                new User(2L, "Owner", "owner@mail.ru"), null),
                        new User(1L, "NameTest", "test@mail.ru"), Status.WAITING),
                new Booking(2L, Instant.now().plusSeconds(3000),
                        Instant.now().plusSeconds(4000),
                        new Item(2L, "Чайник", "Новый чайник", true,
                                new User(2L, "Owner", "owner@mail.ru"), null),
                        new User(1L, "NameTest", "test@mail.ru"), Status.WAITING)).collect(Collectors.toList());
        Page<Booking> page =
                new PageImpl<>(list, pageable, list.size());
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(2L, "Owner", "owner@mail.ru"));
        Mockito
                .when(mockBookingRepository.findAllByOwner_IdByStatus(Mockito.anyLong(), Mockito.anyString(), Mockito.any()))
                .thenReturn(page);

        List<BookingDtoView> resultList = bookingService.getAllBookingsByOwnerId(2L, "WAITING", 0, 5);
        Assertions.assertEquals(2, resultList.size(), "Размер списка не совпадает");
        Assertions.assertEquals(1L, resultList.get(0).getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Отвертка", resultList.get(0).getItem().getName(),
                "Поля объектов не совпадают");
        Assertions.assertEquals("Owner", resultList.get(0).getItem().getOwner().getName(),
                "Поля объектов не совпадают");
        Assertions.assertTrue(resultList.get(0).getItem().isAvailable(), "Поля объектов не совпадают");
        Assertions.assertNull(resultList.get(1).getItem().getRequest(), "Поля объектов не совпадают");
        Assertions.assertEquals(2L, resultList.get(1).getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Чайник", resultList.get(1).getItem().getName(), "Поля объектов не совпадают");
        Assertions.assertEquals("Новый чайник", resultList.get(1).getItem().getDescription(),
                "Поля объектов не совпадают");
    }

    @Test
    void testGetAllBookingsByOwnerIdWithIncorrectUserId() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenThrow(new UserNotFoundException("Пользователь с id 9999 не зарегистрирован "
                        + "в базе приложения."));

        final UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> bookingService.getAllBookingsByOwnerId(9999L, "WAITING", 0, 5));

        Assertions.assertEquals("Пользователь с id 9999 не зарегистрирован в базе приложения.",
                exception.getMessage());
    }

    @Test
    void testGetAllBookingsByOwnerIdWithIncorrectParameterFrom() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(2L, "Owner", "owner@mail.ru"));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.getAllBookingsByOwnerId(2L, "WAITING", -5, 5));

        Assertions.assertEquals("Переданы некорректные параметры постраничного вывода", exception.getMessage());
    }

    @Test
    void testGetAllBookingsByOwnerIdWithIncorrectParameterSize() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(2L, "Owner", "owner@mail.ru"));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.getAllBookingsByOwnerId(2L, "WAITING", 0, -5));

        Assertions.assertEquals("Переданы некорректные параметры постраничного вывода", exception.getMessage());
    }

    @Test
    void testGetAllBookingsByOwnerIdWhenFromEqualTo0AndSizeEqualTo0() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(2L, "Owner", "owner@mail.ru"));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.getAllBookingsByOwnerId(1L, "WAITING", 0, 0));

        Assertions.assertEquals("Переданы некорректные параметры постраничного вывода", exception.getMessage());
    }

    @Test
    void testGetAllBookingsByOwnerIdWhenStateIsREJECTED() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Booking> list = Stream.of(new Booking(1L, Instant.now().plusSeconds(1000),
                        Instant.now().plusSeconds(2000),
                        new Item(1L, "Отвертка", "Крестовая отвертка", true,
                                new User(2L, "Owner", "owner@mail.ru"), null),
                        new User(1L, "NameTest", "test@mail.ru"), Status.REJECTED),
                new Booking(2L, Instant.now().plusSeconds(3000),
                        Instant.now().plusSeconds(4000),
                        new Item(2L, "Чайник", "Новый чайник", true,
                                new User(2L, "Owner", "owner@mail.ru"), null),
                        new User(1L, "NameTest", "test@mail.ru"), Status.REJECTED)).collect(Collectors.toList());
        Page<Booking> page =
                new PageImpl<>(list, pageable, list.size());
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(2L, "Owner", "owner@mail.ru"));
        Mockito
                .when(mockBookingRepository.findAllByOwner_IdByStatus(Mockito.anyLong(), Mockito.anyString(), Mockito.any()))
                .thenReturn(page);

        List<BookingDtoView> resultList = bookingService.getAllBookingsByOwnerId(2L, "REJECTED", 0, 5);
        Assertions.assertEquals(2, resultList.size(), "Размер списка не совпадает");
        Assertions.assertEquals(1L, resultList.get(0).getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Отвертка", resultList.get(0).getItem().getName(),
                "Поля объектов не совпадают");
        Assertions.assertEquals("REJECTED", resultList.get(0).getStatus(),
                "Поля объектов не совпадают");
        Assertions.assertTrue(resultList.get(0).getItem().isAvailable(), "Поля объектов не совпадают");
        Assertions.assertNull(resultList.get(1).getItem().getRequest(), "Поля объектов не совпадают");
        Assertions.assertEquals(2L, resultList.get(1).getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Чайник", resultList.get(1).getItem().getName(), "Поля объектов не совпадают");
        Assertions.assertEquals("REJECTED", resultList.get(1).getStatus(),
                "Поля объектов не совпадают");
    }

    @Test
    void testGetAllBookingsByOwnerIdWhenStateIsALL() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Booking> list = Stream.of(new Booking(1L, Instant.now().plusSeconds(1000),
                        Instant.now().plusSeconds(2000),
                        new Item(1L, "Отвертка", "Крестовая отвертка", true,
                                new User(2L, "Owner", "owner@mail.ru"), null),
                        new User(1L, "NameTest", "test@mail.ru"), Status.WAITING),
                new Booking(2L, Instant.now().plusSeconds(3000),
                        Instant.now().plusSeconds(4000),
                        new Item(2L, "Чайник", "Новый чайник", true,
                                new User(2L, "Owner", "owner@mail.ru"), null),
                        new User(1L, "NameTest", "test@mail.ru"), Status.APPROVED)).collect(Collectors.toList());
        Page<Booking> page =
                new PageImpl<>(list, pageable, list.size());
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(2L, "Owner", "owner@mail.ru"));
        Mockito
                .when(mockBookingRepository.findAllByOwnerId(Mockito.anyLong(), Mockito.any()))
                .thenReturn(page);

        List<BookingDtoView> resultList = bookingService.getAllBookingsByOwnerId(2L, "ALL", 0, 5);
        Assertions.assertEquals(2, resultList.size(), "Размер списка не совпадает");
        Assertions.assertEquals(1L, resultList.get(0).getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Отвертка", resultList.get(0).getItem().getName(),
                "Поля объектов не совпадают");
        Assertions.assertEquals("WAITING", resultList.get(0).getStatus(),
                "Поля объектов не совпадают");
        Assertions.assertTrue(resultList.get(0).getItem().isAvailable(), "Поля объектов не совпадают");
        Assertions.assertNull(resultList.get(1).getItem().getRequest(), "Поля объектов не совпадают");
        Assertions.assertEquals(2L, resultList.get(1).getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Чайник", resultList.get(1).getItem().getName(), "Поля объектов не совпадают");
        Assertions.assertEquals("APPROVED", resultList.get(1).getStatus(),
                "Поля объектов не совпадают");
    }

    @Test
    void testGetAllBookingsByOwnerIdWhenStateIsCURRENT() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Booking> list = Stream.of(new Booking(1L, Instant.now().minusSeconds(1000),
                        Instant.now().plusSeconds(2000),
                        new Item(1L, "Отвертка", "Крестовая отвертка", true,
                                new User(2L, "Owner", "owner@mail.ru"), null),
                        new User(1L, "NameTest", "test@mail.ru"), Status.WAITING),
                new Booking(2L, Instant.now().minusSeconds(3000),
                        Instant.now().plusSeconds(4000),
                        new Item(2L, "Чайник", "Новый чайник", true,
                                new User(2L, "Owner", "owner@mail.ru"), null),
                        new User(1L, "NameTest", "test@mail.ru"), Status.APPROVED)).collect(Collectors.toList());
        Page<Booking> page =
                new PageImpl<>(list, pageable, list.size());
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(2L, "Owner", "owner@mail.ru"));
        Mockito
                .when(mockBookingRepository.findAllByOwnerId(Mockito.anyLong(), Mockito.any()))
                .thenReturn(page);

        List<BookingDtoView> resultList = bookingService.getAllBookingsByOwnerId(2L, "CURRENT", 0, 5);
        Assertions.assertEquals(2, resultList.size(), "Размер списка не совпадает");
        Assertions.assertEquals(1L, resultList.get(0).getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Отвертка", resultList.get(0).getItem().getName(),
                "Поля объектов не совпадают");
        Assertions.assertEquals("WAITING", resultList.get(0).getStatus(),
                "Поля объектов не совпадают");
        Assertions.assertTrue(resultList.get(0).getItem().isAvailable(), "Поля объектов не совпадают");
        Assertions.assertNull(resultList.get(1).getItem().getRequest(), "Поля объектов не совпадают");
        Assertions.assertEquals(2L, resultList.get(1).getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Чайник", resultList.get(1).getItem().getName(), "Поля объектов не совпадают");
        Assertions.assertEquals("APPROVED", resultList.get(1).getStatus(),
                "Поля объектов не совпадают");
    }

    @Test
    void testGetAllBookingsByOwnerIdWhenStateIsFUTURE() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Booking> list = Stream.of(new Booking(1L, Instant.now().plusSeconds(1000),
                        Instant.now().plusSeconds(2000),
                        new Item(1L, "Отвертка", "Крестовая отвертка", true,
                                new User(2L, "Owner", "owner@mail.ru"), null),
                        new User(1L, "NameTest", "test@mail.ru"), Status.WAITING),
                new Booking(2L, Instant.now().minusSeconds(3000),
                        Instant.now().plusSeconds(4000),
                        new Item(2L, "Чайник", "Новый чайник", true,
                                new User(2L, "Owner", "owner@mail.ru"), null),
                        new User(1L, "NameTest", "test@mail.ru"), Status.APPROVED)).collect(Collectors.toList());
        Page<Booking> page =
                new PageImpl<>(list, pageable, list.size());
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(2L, "Owner", "owner@mail.ru"));
        Mockito
                .when(mockBookingRepository.findAllByOwnerId(Mockito.anyLong(), Mockito.any()))
                .thenReturn(page);

        List<BookingDtoView> resultList = bookingService.getAllBookingsByOwnerId(2L, "FUTURE", 0, 5);
        Assertions.assertEquals(1, resultList.size(), "Размер списка не совпадает");
        Assertions.assertEquals(1L, resultList.get(0).getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Отвертка", resultList.get(0).getItem().getName(),
                "Поля объектов не совпадают");
        Assertions.assertEquals("WAITING", resultList.get(0).getStatus(),
                "Поля объектов не совпадают");
        Assertions.assertTrue(resultList.get(0).getItem().isAvailable(), "Поля объектов не совпадают");
    }

    @Test
    void testGetAllBookingsByOwnerIdWhenStateIsPAST() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Booking> list = Stream.of(new Booking(1L, Instant.now().plusSeconds(1000),
                        Instant.now().plusSeconds(2000),
                        new Item(1L, "Отвертка", "Крестовая отвертка", true,
                                new User(2L, "Owner", "owner@mail.ru"), null),
                        new User(1L, "NameTest", "test@mail.ru"), Status.WAITING),
                new Booking(2L, Instant.now().minusSeconds(4000),
                        Instant.now().minusSeconds(3000),
                        new Item(2L, "Чайник", "Новый чайник", true,
                                new User(2L, "Owner", "owner@mail.ru"), null),
                        new User(1L, "NameTest", "test@mail.ru"), Status.APPROVED)).collect(Collectors.toList());
        Page<Booking> page =
                new PageImpl<>(list, pageable, list.size());
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(2L, "Owner", "owner@mail.ru"));
        Mockito
                .when(mockBookingRepository.findAllByOwnerId(Mockito.anyLong(), Mockito.any()))
                .thenReturn(page);

        List<BookingDtoView> resultList = bookingService.getAllBookingsByOwnerId(2L, "PAST", 0, 5);
        Assertions.assertEquals(1, resultList.size(), "Размер списка не совпадает");
        Assertions.assertEquals(2L, resultList.get(0).getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Чайник", resultList.get(0).getItem().getName(),
                "Поля объектов не совпадают");
        Assertions.assertEquals("APPROVED", resultList.get(0).getStatus(),
                "Поля объектов не совпадают");
        Assertions.assertTrue(resultList.get(0).getItem().isAvailable(), "Поля объектов не совпадают");
    }

    @Test
    void testGetAllBookingsByOwnerIdWhenStateIncorrect() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(2L, "Owner", "owner@mail.ru"));

        final ParameterStateException exception = Assertions.assertThrows(
                ParameterStateException.class,
                () -> bookingService.getAllBookingsByOwnerId(2L, "SOMETHING", 0, 5));

        Assertions.assertEquals("Unknown state: SOMETHING", exception.getMessage());
    }
}
