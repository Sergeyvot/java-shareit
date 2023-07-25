package ru.practicum.shareit.request;

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
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoView;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
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
public class ItemRequestServiceTest {
    @Mock
    ItemRequestRepository mockItemRequestRepository;
    @Mock
    UserService mockUserService;
    @Autowired
    UserMapper userMapper;
    @Autowired
    ItemRequestMapper itemRequestMapper;
    @Autowired
    ItemMapper itemMapper;
    @Mock
    ItemRepository mockItemRepository;
    ItemRequestService requestService;

    @BeforeEach
    @Test
    void initializingService() {
        requestService = new ItemRequestServiceImpl(mockItemRequestRepository, mockUserService, userMapper,
                itemRequestMapper, itemMapper, mockItemRepository);
    }

    @Test
    void testCreateNewRequest() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(1L, "NameTest", "test@mail.ru"));
        Mockito
                .when(mockItemRequestRepository.save(Mockito.any()))
                .thenReturn(new ItemRequest(1L, "Нужна отвертка",
                        new User(1L, "NameTest", "test@mail.ru"), Instant.now()));
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("Нужна отвертка").build();

        ItemRequestDto newItemRequest = requestService.createNewRequest(1L, itemRequestDto);
        Assertions.assertEquals(1L, newItemRequest.getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Нужна отвертка", newItemRequest.getDescription(),
                "Поля объектов не совпадают");
    }

    @Test
    void testCreateNewRequestWithIncorrectUserId() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(null);
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("Нужна отвертка").build();

        final UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> requestService.createNewRequest(999L, itemRequestDto));

        Assertions.assertEquals("Пользователь с id 999 не зарегистрирован "
                + "в базе приложения.", exception.getMessage());
    }

    @Test
    void testCreateNewRequestWithEmptyRequestBody() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(1L, "NameTest", "test@mail.ru"));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> requestService.createNewRequest(1L, null));

        Assertions.assertEquals("Тело запроса не может быть пустым.", exception.getMessage());
    }

    @Test
    void testCreateNewRequestWithEmptyDescription() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(1L, "NameTest", "test@mail.ru"));
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("").build();

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> requestService.createNewRequest(1L, itemRequestDto));

        Assertions.assertEquals("Описание вещи не может быть пустым", exception.getMessage());
    }

    @Test
    void testFindEntityById() {
        Mockito
                .when(mockItemRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new ItemRequest(1L, "Нужна отвертка",
                        new User(1L, "NameTest", "test@mail.ru"), Instant.now())));

        ItemRequest itemRequest = requestService.findEntityById(1L);

        Assertions.assertEquals(1L, itemRequest.getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Нужна отвертка", itemRequest.getDescription(),
                "Поля объектов не совпадают");
        Assertions.assertEquals("NameTest", itemRequest.getRequestor().getName(),
                "Поля объектов не совпадают");
    }

    @Test
    void testFindEntityByIdWithIncorrectId() {
        Mockito
                .when(mockItemRequestRepository.findById(Mockito.anyLong()))
                .thenThrow(new RequestNotFoundException("Запрос с id 999 не зарегистрирован "
                        + "в приложении."));

        final RequestNotFoundException exception = Assertions.assertThrows(
                RequestNotFoundException.class,
                () -> requestService.findEntityById(999L));

        Assertions.assertEquals("Запрос с id 999 не зарегистрирован "
                + "в приложении.", exception.getMessage());
    }

    @Test
    void testGetAllByRequesterId() {
        List<ItemRequest> list = Stream.of(new ItemRequest(1L, "Нужна отвертка",
                        new User(1L, "NameTest", "test@mail.ru"), Instant.now()),
                new ItemRequest(2L, "Нужен чайник",
                        new User(1L, "NameTest", "test@mail.ru"), Instant.now()))
                        .collect(Collectors.toList());
        List<Item> items = Stream.of(new Item(2L, "Чайник", "Почти новый", true,
                new User(2L, "Owner", "owner@mail.ru"), new ItemRequest(2L, "Нужен чайник",
                new User(1L, "NameTest", "test@mail.ru"), Instant.now()))).collect(Collectors.toList());
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(1L, "NameTest", "test@mail.ru"));
        Mockito
                .when(mockItemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(Mockito.anyLong()))
                .thenReturn(list);
        Mockito
                .when(mockItemRepository.findAll())
                .thenReturn(items);

        List<ItemRequestDtoView> resultList = requestService.getAllByRequesterId(1L);
        Assertions.assertEquals(2, resultList.size(), "Размер списка не совпадает");
        Assertions.assertEquals(1L, resultList.get(0).getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Нужна отвертка", resultList.get(0).getDescription(),
                "Поля объектов не совпадают");
        Assertions.assertEquals("Нужен чайник",resultList.get(1).getDescription(),
                "Поля объектов не совпадают");
        Assertions.assertEquals("Почти новый",resultList.get(1).getItems().get(0).getDescription(),
                "Поля объектов не совпадают");
        Assertions.assertEquals(2L,resultList.get(1).getItems().get(0).getRequestId(),
                "Поля объектов не совпадают");
    }

    @Test
    void testGetAllByRequesterIdWithIncorrectRequesterId() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(null);

        final UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> requestService.getAllByRequesterId(999L));

        Assertions.assertEquals("Пользователь с id 999 не зарегистрирован "
                + "в базе приложения.", exception.getMessage());
    }

    @Test
    void testGetAllRequestsOfOtherUsers() {
        Pageable pageable = PageRequest.of(0, 5);
        List<ItemRequest> list = Stream.of(new ItemRequest(1L, "Нужна отвертка",
                                new User(1L, "NameTest", "test@mail.ru"), Instant.now()),
                        new ItemRequest(2L, "Нужен чайник",
                                new User(1L, "NameTest", "test@mail.ru"), Instant.now()))
                .collect(Collectors.toList());
        Page<ItemRequest> page =
                new PageImpl<>(list, pageable, list.size());
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(2L, "NameTest", "test@mail.ru"));
        Mockito
                .when(mockItemRequestRepository.findAllOrderByCreatedDesc(Mockito.anyLong(), Mockito.any()))
                .thenReturn(page);
        Mockito
                .when(mockItemRepository.findAll())
                .thenReturn(new ArrayList<>());

        List<ItemRequestDtoView> resultList = requestService.getAllRequestsOfOtherUsers(2L,0, 5);
        Assertions.assertEquals(2, resultList.size(), "Размер списка не совпадает");
        Assertions.assertEquals(1L, resultList.get(0).getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Нужна отвертка", resultList.get(0).getDescription(),
                "Поля объектов не совпадают");
        Assertions.assertEquals("Нужен чайник",resultList.get(1).getDescription(),
                "Поля объектов не совпадают");
        Assertions.assertTrue(resultList.get(0).getItems().isEmpty(),
                "Поля объектов не совпадают");
        Assertions.assertTrue(resultList.get(1).getItems().isEmpty(),
                "Поля объектов не совпадают");
    }

    @Test
    void testGetAllRequestsOfOtherUsersWithIncorrectParameterFrom() {

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> requestService.getAllRequestsOfOtherUsers(2L,-5, 5));

        Assertions.assertEquals("Переданы некорректные параметры постраничного вывода", exception.getMessage());
    }

    @Test
    void testGetAllRequestsOfOtherUsersWithIncorrectParameterSize() {

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> requestService.getAllRequestsOfOtherUsers(2L,0, -5));

        Assertions.assertEquals("Переданы некорректные параметры постраничного вывода", exception.getMessage());
    }

    @Test
    void testGetAllRequestsOfOtherUsersWhenFromEqualTo0AndSizeEqualTo0() {

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> requestService.getAllRequestsOfOtherUsers(2L,0, 0));

        Assertions.assertEquals("Переданы некорректные параметры постраничного вывода", exception.getMessage());
    }

    @Test
    void testGetAllRequestsOfOtherUsersWithIncorrectUserId() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenThrow(new UserNotFoundException("Пользователь с id 9999 не зарегистрирован "
                        + "в базе приложения."));

        final UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> requestService.getAllRequestsOfOtherUsers(9999L,0, 5));

        Assertions.assertEquals("Пользователь с id 9999 не зарегистрирован в базе приложения.",
                exception.getMessage());
    }

    @Test
    void testFindRequestById() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(2L, "NameTest", "test@mail.ru"));
        Mockito
                .when(mockItemRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new ItemRequest(1L, "Нужна отвертка",
                        new User(1L, "NameTest", "test@mail.ru"), Instant.now())));
        Mockito
                .when(mockItemRepository.findAll())
                .thenReturn(new ArrayList<>());

        ItemRequestDtoView result = requestService.findRequestById(1L, 2L);
        Assertions.assertEquals(1L, result.getId(), "Поля объектов не совпадают");
        Assertions.assertEquals("Нужна отвертка", result.getDescription(),
                "Поля объектов не совпадают");
        Assertions.assertTrue(result.getItems().isEmpty(),
                "Поля объектов не совпадают");
    }

    @Test
    void testFindRequestByIdWithIncorrectUserId() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenThrow(new UserNotFoundException("Пользователь с id 9999 не зарегистрирован "
                        + "в базе приложения."));

        final UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> requestService.findRequestById(1L, 9999L));

        Assertions.assertEquals("Пользователь с id 9999 не зарегистрирован в базе приложения.",
                exception.getMessage());
    }

    @Test
    void testFindRequestByIdWithIncorrectRequestId() {
        Mockito
                .when(mockUserService.findUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(2L, "NameTest", "test@mail.ru"));
        Mockito
                .when(mockItemRequestRepository.findById(Mockito.anyLong()))
                .thenThrow(new RequestNotFoundException("Запрос с id 999 не зарегистрирован "
                        + "в приложении."));

        final RequestNotFoundException exception = Assertions.assertThrows(
                RequestNotFoundException.class,
                () -> requestService.findRequestById(999L, 2L));

        Assertions.assertEquals("Запрос с id 999 не зарегистрирован "
                + "в приложении.", exception.getMessage());
    }
}
