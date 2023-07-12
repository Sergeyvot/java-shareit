package ru.practicum.shareit.item;

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
import ru.practicum.shareit.booking.BookingDtoItemMapper;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    ItemRepository mockItemRepository;
    @Mock
    BookingRepository mockBookingRepository;
    @Mock
    CommentRepository mockCommentRepository;
    @Mock
    UserService mockUserService;
    @Mock
    ItemRequestService mockRequestService;
    @Autowired
    UserMapper userMapper;
    @Autowired
    ItemMapper itemMapper;
    @Autowired
    BookingDtoItemMapper bookingDtoItemMapper;
    ItemService itemService;

    @BeforeEach
    @Test
    void initializingService() {
        itemService = new ItemServiceImpl(mockItemRepository, mockBookingRepository, mockCommentRepository,
                mockUserService, userMapper, itemMapper, bookingDtoItemMapper, mockRequestService);
    }

    @Test
    void testAddNewItem() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(1L, "NameTest", "test@mail.ru"));
        Mockito
                .when(mockRequestService.findEntityById(Mockito.anyLong()))
                .thenReturn(new ItemRequest(3L, "Надо отвертку",
                        new User(2L, "Requester", "test1@mail.ru"), Instant.now()));
        Mockito
                .when(mockItemRepository.save(Mockito.any()))
                .thenReturn(new Item(1L, "Отвертка", "Крестовая отвертка", true,
                        new User(1L, "NameTest", "test@mail.ru"),
                        new ItemRequest(3L, "Надо отвертку",
                                new User(2L, "Requester", "test1@mail.ru"), Instant.now())));

        ItemDto itemDto = ItemDto.builder()
                .name("Отвертка")
                .description("Крестовая отвертка")
                .available(true)
                .requestId(3L).build();

        ItemDto newItemDto = itemService.addNewItem(1L, itemDto);
        Assertions.assertEquals(1L, newItemDto.getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Отвертка", newItemDto.getName(), "Поля объектов не совпадают");
        Assertions.assertEquals(true, newItemDto.getAvailable(), "Поля объектов не совпадают");
        Assertions.assertEquals(3L, newItemDto.getRequestId(), "Поля объектов не совпадают");
    }

    @Test
    void testAddNewItemWithEmptyBodyRequest() {

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.addNewItem(1L, null));

        Assertions.assertEquals("Тело запроса не может быть пустым.", exception.getMessage());
    }

    @Test
    void testAddNewItemWithEmptyName() {
        ItemDto itemDto = ItemDto.builder()
                .name("")
                .description("Крестовая отвертка")
                .available(true)
                .requestId(3L).build();

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.addNewItem(1L, itemDto));

        Assertions.assertEquals("Название вещи не может быть пустым", exception.getMessage());
    }

    @Test
    void testAddNewItemWithEmptyDescription() {
        ItemDto itemDto = ItemDto.builder()
                .name("Отвертка")
                .description("")
                .available(true)
                .requestId(3L).build();

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.addNewItem(1L, itemDto));

        Assertions.assertEquals("Описание вещи не может быть пустым", exception.getMessage());
    }

    @Test
    void testAddNewItemWithEmptyAvailable() {
        ItemDto itemDto = ItemDto.builder()
                .name("Отвертка")
                .description("Крестовая отвертка")
                .requestId(3L).build();

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.addNewItem(1L, itemDto));

        Assertions.assertEquals("При создании вещи должна быть указана ее доступность. "
                + "Поле не может быть пустым", exception.getMessage());
    }

    @Test
    void testAddNewItemWithEmptyRequestId() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(1L, "NameTest", "test@mail.ru"));
        Mockito
                .when(mockItemRepository.save(Mockito.any()))
                .thenReturn(new Item(1L, "Отвертка", "Крестовая отвертка", true,
                        new User(1L, "NameTest", "test@mail.ru"), null));

        ItemDto itemDto = ItemDto.builder()
                .name("Отвертка")
                .description("Крестовая отвертка")
                .available(true).build();

        ItemDto newItemDto = itemService.addNewItem(1L, itemDto);
        Assertions.assertEquals(1L, newItemDto.getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Отвертка", newItemDto.getName(), "Поля объектов не совпадают");
        Assertions.assertEquals(true, newItemDto.getAvailable(), "Поля объектов не совпадают");
        Assertions.assertNull(newItemDto.getRequestId(), "Поля объектов не совпадают");
    }

    @Test
    void testUpdateItem() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(1L, "NameTest", "test@mail.ru"));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Item(1L, "Отвертка", "Крестовая отвертка", true,
                        new User(1L, "NameTest", "test@mail.ru"), null)));
        Mockito
                .when(mockItemRepository.save(Mockito.any()))
                .thenReturn(new Item(1L, "Чайник", "Новый чайник", false,
                        new User(1L, "NameTest", "test@mail.ru"), null));

        ItemDto itemDto = ItemDto.builder()
                .name("Чайник")
                .description("Новый чайник")
                .available(false).build();

        ItemDto updateItemDto = itemService.updateItem(1L, 1L, itemDto);
        Assertions.assertEquals(1L, updateItemDto.getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Чайник", updateItemDto.getName(), "Поля объектов не совпадают");
        Assertions.assertEquals(false, updateItemDto.getAvailable(), "Поля объектов не совпадают");
        Assertions.assertEquals("Новый чайник", updateItemDto.getDescription(), "Поля объектов не совпадают");
    }

    @Test
    void testUpdateItemWithIncorrectUserId() {
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Item(1L, "Отвертка", "Крестовая отвертка", true,
                        new User(1L, "NameTest", "test@mail.ru"), null)));
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenThrow(new UserNotFoundException("Пользователь с id 9999 не зарегистрирован "
                        + "в базе приложения."));

        ItemDto itemDto = ItemDto.builder()
                .name("Чайник")
                .description("Новый чайник")
                .available(false).build();

        final UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> itemService.updateItem(9999, 1L, itemDto));

        Assertions.assertEquals("Пользователь с id 9999 не зарегистрирован в базе приложения.",
                exception.getMessage());
    }

    @Test
    void testUpdateItemWithIncorrectItemId() {
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenThrow(new ItemNotFoundException("Вещь с id 9999 не зарегистрирована "
                        + "в приложении."));
        ItemDto itemDto = ItemDto.builder()
                .name("Чайник")
                .description("Новый чайник")
                .available(false).build();

        final ItemNotFoundException exception = Assertions.assertThrows(
                ItemNotFoundException.class,
                () -> itemService.updateItem(1L, 9999, itemDto));
        Assertions.assertEquals("Вещь с id 9999 не зарегистрирована в приложении.",
                exception.getMessage());
    }

    @Test
    void testUpdateItemWithIncorrectOwnerId() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(2L, "NameTest", "test@mail.ru"));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Item(1L, "Отвертка", "Крестовая отвертка", true,
                        new User(1L, "OtherName", "test1@mail.ru"), null)));
        ItemDto itemDto = ItemDto.builder()
                .name("Чайник")
                .description("Новый чайник")
                .available(false).build();

        final UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> itemService.updateItem(2L, 1L, itemDto));
        Assertions.assertEquals("Редактировать вещь c id 1 может только ее владелец с id 1",
                exception.getMessage());
    }

    @Test
    void testUpdateItemWithEmptyUpdateName() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(1L, "NameTest", "test@mail.ru"));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Item(1L, "Отвертка", "Крестовая отвертка", true,
                        new User(1L, "NameTest", "test@mail.ru"), null)));
        Mockito
                .when(mockItemRepository.save(Mockito.any()))
                .thenReturn(new Item(1L, "Отвертка", "Новый чайник", false,
                        new User(1L, "NameTest", "test@mail.ru"), null));

        ItemDto itemDto = ItemDto.builder()
                .description("Новый чайник")
                .available(false).build();

        ItemDto updateItemDto = itemService.updateItem(1L, 1L, itemDto);
        Assertions.assertEquals(1L, updateItemDto.getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Отвертка", updateItemDto.getName(), "Поля объектов не совпадают");
        Assertions.assertEquals(false, updateItemDto.getAvailable(), "Поля объектов не совпадают");
        Assertions.assertEquals("Новый чайник", updateItemDto.getDescription(), "Поля объектов не совпадают");
    }

    @Test
    void testUpdateItemWithEmptyUpdateDescription() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(1L, "NameTest", "test@mail.ru"));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Item(1L, "Отвертка", "Крестовая отвертка", true,
                        new User(1L, "NameTest", "test@mail.ru"), null)));
        Mockito
                .when(mockItemRepository.save(Mockito.any()))
                .thenReturn(new Item(1L, "Чайник", "Крестовая отвертка", false,
                        new User(1L, "NameTest", "test@mail.ru"), null));

        ItemDto itemDto = ItemDto.builder()
                .name("Чайник")
                .available(false).build();

        ItemDto updateItemDto = itemService.updateItem(1L, 1L, itemDto);
        Assertions.assertEquals(1L, updateItemDto.getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Чайник", updateItemDto.getName(), "Поля объектов не совпадают");
        Assertions.assertEquals(false, updateItemDto.getAvailable(), "Поля объектов не совпадают");
        Assertions.assertEquals("Крестовая отвертка", updateItemDto.getDescription(), "Поля объектов не совпадают");
    }

    @Test
    void testUpdateItemWithEmptyUpdateAvailable() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(1L, "NameTest", "test@mail.ru"));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Item(1L, "Отвертка", "Крестовая отвертка", true,
                        new User(1L, "NameTest", "test@mail.ru"), null)));
        Mockito
                .when(mockItemRepository.save(Mockito.any()))
                .thenReturn(new Item(1L, "Чайник", "Новый чайник", true,
                        new User(1L, "NameTest", "test@mail.ru"), null));

        ItemDto itemDto = ItemDto.builder()
                .name("Чайник")
                .description("Новый чайник").build();

        ItemDto updateItemDto = itemService.updateItem(1L, 1L, itemDto);
        Assertions.assertEquals(1L, updateItemDto.getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Чайник", updateItemDto.getName(), "Поля объектов не совпадают");
        Assertions.assertEquals(true, updateItemDto.getAvailable(), "Поля объектов не совпадают");
        Assertions.assertEquals("Новый чайник", updateItemDto.getDescription(), "Поля объектов не совпадают");
    }

    @Test
    void testAddNewComment() {
        List<Booking> bookings = Stream.of(new Booking(1L, Instant.now().minusSeconds(2000),
                Instant.now().minusSeconds(1000),
                new Item(1L, "Отвертка", "Крестовая отвертка", true,
                        new User(1L, "NameTest", "test@mail.ru"), null),
                new User(1L, "NameTest", "test@mail.ru"), Status.APPROVED)).collect(Collectors.toList());
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(1L, "NameTest", "test@mail.ru"));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Item(1L, "Отвертка", "Крестовая отвертка", true,
                        new User(1L, "NameTest", "test@mail.ru"), null)));
        Mockito
                .when(mockBookingRepository.findAllByItemId(Mockito.anyLong()))
                .thenReturn(bookings);
        Mockito
                .when(mockCommentRepository.save(Mockito.any()))
                .thenReturn(new Comment(1L, "Все понравилось", new Item(1L, "Отвертка", "Крестовая отвертка", true,
                        new User(1L, "NameTest", "test@mail.ru"), null),
                        new User(1L, "NameTest", "test@mail.ru"), Instant.now()));

        CommentDto commentDto = CommentDto.builder()
                .text("Все понравилось").build();
        CommentDto newComment = itemService.addNewComment(1L, 1L, commentDto);
        Assertions.assertEquals(1L, newComment.getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Все понравилось", newComment.getText(), "Поля объектов не совпадают");
        Assertions.assertEquals("NameTest", newComment.getAuthorName(), "Поля объектов не совпадают");
    }

    @Test
    void testAddNewCommentWithIncorrectUserId() {
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Item(1L, "Отвертка", "Крестовая отвертка", true,
                        new User(1L, "NameTest", "test@mail.ru"), null)));
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenThrow(new UserNotFoundException("Пользователь с id 9999 не зарегистрирован "
                        + "в базе приложения."));

        CommentDto commentDto = CommentDto.builder()
                .text("Все понравилось").build();

        final UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> itemService.addNewComment(9999, 1L, commentDto));

        Assertions.assertEquals("Пользователь с id 9999 не зарегистрирован в базе приложения.",
                exception.getMessage());
    }

    @Test
    void testAddNewCommentWithIncorrectItemId() {
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenThrow(new ItemNotFoundException("Вещь с id 9999 не зарегистрирована "
                        + "в приложении."));
        CommentDto commentDto = CommentDto.builder()
                .text("Все понравилось").build();

        final ItemNotFoundException exception = Assertions.assertThrows(
                ItemNotFoundException.class,
                () -> itemService.addNewComment(1L, 9999, commentDto));
        Assertions.assertEquals("Вещь с id 9999 не зарегистрирована в приложении.",
                exception.getMessage());
    }

    @Test
    void testAddNewCommentWithEmptyComment() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(1L, "NameTest", "test@mail.ru"));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Item(1L, "Отвертка", "Крестовая отвертка", true,
                        new User(1L, "NameTest", "test@mail.ru"), null)));

        CommentDto commentDto = CommentDto.builder()
                .text("").build();

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.addNewComment(1L, 1L, commentDto));

        Assertions.assertEquals("Отзыв не может быть пустым сообщением", exception.getMessage());
    }

    @Test
    void testAddNewCommentWithIncorrectAuthorId() {
        List<Booking> bookings = Stream.of(new Booking(1L, Instant.now().minusSeconds(2000),
                Instant.now().minusSeconds(1000),
                new Item(1L, "Отвертка", "Крестовая отвертка", true,
                        new User(2L, "NameTest", "test@mail.ru"), null),
                new User(3L, "NameTest", "test@mail.ru"), Status.APPROVED)).collect(Collectors.toList());
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(1L, "NameTest", "test@mail.ru"));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Item(1L, "Отвертка", "Крестовая отвертка", true,
                        new User(2L, "NameTest", "test@mail.ru"), null)));
        Mockito
                .when(mockBookingRepository.findAllByItemId(Mockito.anyLong()))
                .thenReturn(bookings);

        CommentDto commentDto = CommentDto.builder()
                .text("Все понравилось").build();
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.addNewComment(1L, 1L, commentDto));

        Assertions.assertEquals("Пользователь с id 1 не брал данную вещь в аренду.", exception.getMessage());
    }

    @Test
    void testAddNewCommentWithBookingEndInFuture() {
        List<Booking> bookings = Stream.of(new Booking(1L, Instant.now().minusSeconds(2000),
                Instant.now().plusSeconds(1000),
                new Item(1L, "Отвертка", "Крестовая отвертка", true,
                        new User(2L, "NameTest", "test@mail.ru"), null),
                new User(1L, "NameTest", "test@mail.ru"), Status.APPROVED)).collect(Collectors.toList());
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(1L, "NameTest", "test@mail.ru"));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Item(1L, "Отвертка", "Крестовая отвертка", true,
                        new User(2L, "NameTest", "test@mail.ru"), null)));
        Mockito
                .when(mockBookingRepository.findAllByItemId(Mockito.anyLong()))
                .thenReturn(bookings);

        CommentDto commentDto = CommentDto.builder()
                .text("Все понравилось").build();
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.addNewComment(1L, 1L, commentDto));

        Assertions.assertEquals("Пользователь с id 1 не брал данную вещь в аренду.", exception.getMessage());
    }

    @Test
    void testFindItemById() {
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Item(1L, "Отвертка", "Крестовая отвертка", true,
                        new User(2L, "NameTest", "test@mail.ru"), null)));
        Mockito
                .when(mockBookingRepository.findByItemIdOrderByEndDesc(Mockito.anyLong(), Mockito.any()))
                .thenReturn(new ArrayList<>());
        Mockito
                .when(mockBookingRepository.findByItemIdOrderByStartAsc(Mockito.anyLong(), Mockito.any()))
                .thenReturn(new ArrayList<>());
        Mockito
                .when(mockCommentRepository.findAllByItemId(Mockito.anyLong()))
                .thenReturn(new ArrayList<>());

        ItemDtoBooking itemDtoBooking = itemService.findItemById(1L, 1L);
        Assertions.assertEquals(1L, itemDtoBooking.getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Отвертка", itemDtoBooking.getName(), "Поля объектов не совпадают");
        Assertions.assertEquals("Крестовая отвертка", itemDtoBooking.getDescription(),
                "Поля объектов не совпадают");
        Assertions.assertEquals(true, itemDtoBooking.getAvailable(), "Поля объектов не совпадают");
        Assertions.assertNull(itemDtoBooking.getLastBooking(), "Поля объектов не совпадают");
        Assertions.assertNull(itemDtoBooking.getNextBooking(), "Поля объектов не совпадают");
        Assertions.assertTrue(itemDtoBooking.getComments().isEmpty(), "Поля объектов не совпадают");
    }

    @Test
    void testFindItemByIdWithIncorrectItemId() {
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenThrow(new ItemNotFoundException("Вещь с id 9999 не зарегистрирована "
                        + "в приложении."));

        final ItemNotFoundException exception = Assertions.assertThrows(
                ItemNotFoundException.class,
                () -> itemService.findItemById(9999, 1L));

        Assertions.assertEquals("Вещь с id 9999 не зарегистрирована в приложении.",
                exception.getMessage());
    }

    @Test
    void testFindEntityById() {
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Item(1L, "Отвертка", "Крестовая отвертка", true,
                        new User(2L, "NameTest", "test@mail.ru"), null)));

        Item item = itemService.findEntityById(1L);
        Assertions.assertEquals(1L, item.getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Отвертка", item.getName(), "Поля объектов не совпадают");
        Assertions.assertEquals("Крестовая отвертка", item.getDescription(),
                "Поля объектов не совпадают");
        Assertions.assertTrue(item.isAvailable(), "Поля объектов не совпадают");
        Assertions.assertEquals(2L, item.getOwner().getId(), "Поля объектов не совпадают");
        Assertions.assertNull(item.getRequest(), "Поля объектов не совпадают");
    }

    @Test
    void testFindEntityByIdWithIncorrectItemId() {
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenThrow(new ItemNotFoundException("Вещь с id 9999 не зарегистрирована "
                        + "в приложении."));

        final ItemNotFoundException exception = Assertions.assertThrows(
                ItemNotFoundException.class,
                () -> itemService.findEntityById(9999));

        Assertions.assertEquals("Вещь с id 9999 не зарегистрирована в приложении.",
                exception.getMessage());
    }

    @Test
    void testGetAllItemsByOwnerId() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Item> list = Stream.of(new Item(1L, "Отвертка", "Крестовая отвертка", true,
                        new User(2L, "NameTest", "test@mail.ru"), null),
                new Item(2L, "Чайник", "Новый чайник", true,
                        new User(2L, "NameTest", "test@mail.ru"), null)).collect(Collectors.toList());
        Page<Item> page =
                new PageImpl<>(list, pageable, list.size());

        Mockito
                .when(mockItemRepository.findAllByOwnerId(Mockito.anyLong(), Mockito.any()))
                .thenReturn(page);
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(1L, "NameTest", "test@mail.ru"));
        Mockito
                .when(mockBookingRepository.findByItemIdOrderByEndDesc(Mockito.anyLong(), Mockito.any()))
                .thenReturn(new ArrayList<>());
        Mockito
                .when(mockBookingRepository.findByItemIdOrderByStartAsc(Mockito.anyLong(), Mockito.any()))
                .thenReturn(new ArrayList<>());
        Mockito
                .when(mockCommentRepository.findAllByItemId(Mockito.anyLong()))
                .thenReturn(new ArrayList<>());

        List<ItemDtoBooking> checkList = itemService.getAllItemsByOwnerId(2L, 0, 5);
        Assertions.assertEquals(1L, checkList.get(0).getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Отвертка", checkList.get(0).getName(), "Поля объектов не совпадают");
        Assertions.assertEquals("Крестовая отвертка", checkList.get(0).getDescription(),
                "Поля объектов не совпадают");
        Assertions.assertTrue(checkList.get(0).getAvailable(), "Поля объектов не совпадают");
        Assertions.assertNull(checkList.get(0).getRequestId(), "Поля объектов не совпадают");
        Assertions.assertEquals(2L, checkList.get(1).getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Чайник", checkList.get(1).getName(), "Поля объектов не совпадают");
        Assertions.assertEquals("Новый чайник", checkList.get(1).getDescription(),
                "Поля объектов не совпадают");
    }

    @Test
    void testGetAllItemsByOwnerIdWithIncorrectUserId() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenThrow(new UserNotFoundException("Пользователь с id 9999 не зарегистрирован "
                        + "в базе приложения."));

        final UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> itemService.getAllItemsByOwnerId(9999, 0, 5));

        Assertions.assertEquals("Пользователь с id 9999 не зарегистрирован в базе приложения.",
                exception.getMessage());
    }

    @Test
    void testGetAllItemsByOwnerIdWithIncorrectParameterFrom() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(1L, "NameTest", "test@mail.ru"));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.getAllItemsByOwnerId(1L, -5, 5));

        Assertions.assertEquals("Переданы некорректные параметры постраничного вывода", exception.getMessage());
    }

    @Test
    void testGetAllItemsByOwnerIdWithIncorrectParameterSize() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(1L, "NameTest", "test@mail.ru"));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.getAllItemsByOwnerId(1L, 0, -5));

        Assertions.assertEquals("Переданы некорректные параметры постраничного вывода", exception.getMessage());
    }

    @Test
    void testGetAllItemsByOwnerIdWhenFromEqualTo0AndSizeEqualTo0() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(1L, "NameTest", "test@mail.ru"));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.getAllItemsByOwnerId(1L, 0, 0));

        Assertions.assertEquals("Переданы некорректные параметры постраничного вывода", exception.getMessage());
    }

    @Test
    void testGetItemBySearch() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Item> list = Stream.of(new Item(1L, "Отвертка", "Крестовая отвертка", true,
                        new User(2L, "NameTest", "test@mail.ru"), null),
                new Item(2L, "Дрель", "Почти отвертка", true,
                        new User(2L, "NameTest", "test@mail.ru"), null)).collect(Collectors.toList());
        Page<Item> page =
                new PageImpl<>(list, pageable, list.size());
        Mockito
                .when(mockItemRepository.search(Mockito.anyString(), Mockito.any()))
                .thenReturn(page);

        List<ItemDto> checkList = itemService.getItemBySearch("ОтВЕрт", 0, 5);
        Assertions.assertEquals(1L, checkList.get(0).getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Отвертка", checkList.get(0).getName(), "Поля объектов не совпадают");
        Assertions.assertEquals("Крестовая отвертка", checkList.get(0).getDescription(),
                "Поля объектов не совпадают");
        Assertions.assertTrue(checkList.get(0).getAvailable(), "Поля объектов не совпадают");
        Assertions.assertNull(checkList.get(0).getRequestId(), "Поля объектов не совпадают");
        Assertions.assertEquals(2L, checkList.get(1).getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Дрель", checkList.get(1).getName(), "Поля объектов не совпадают");
        Assertions.assertEquals("Почти отвертка", checkList.get(1).getDescription(),
                "Поля объектов не совпадают");
    }

    @Test
    void testGetItemBySearchWithoutParameterText() {

        List<ItemDto> checkList = itemService.getItemBySearch(null, 0, 5);
        Assertions.assertTrue(checkList.isEmpty(), "Список не совпадает");
    }

    @Test
    void testGetItemBySearchWithIncorrectParameterFrom() {

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.getItemBySearch("ОтВЕрт", -5, 5));

        Assertions.assertEquals("Переданы некорректные параметры постраничного вывода", exception.getMessage());
    }

    @Test
    void testGetItemBySearchWithIncorrectParameterSize() {

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.getItemBySearch("ОтВЕрт", 0, -5));

        Assertions.assertEquals("Переданы некорректные параметры постраничного вывода", exception.getMessage());
    }

    @Test
    void testGetItemBySearchWhenFromEqualTo0AndSizeEqualTo0() {

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.getItemBySearch("ОтВЕрт", 0, 0));

        Assertions.assertEquals("Переданы некорректные параметры постраничного вывода", exception.getMessage());
    }
}
