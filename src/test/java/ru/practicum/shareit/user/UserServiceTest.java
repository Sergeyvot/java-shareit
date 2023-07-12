package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.DuplicateEmailUserException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository mockUserRepository;
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserService userService;

    @BeforeEach
    @Test
    void initializingService() {
        userService = new UserServiceImpl(mockUserRepository, userMapper);
    }

    @Test
    void testCreateUser() {

        Mockito
                .when(mockUserRepository.save(Mockito.any()))
                .thenReturn(new User(1L, "NameTest", "test@mail.ru"));
        UserDto userDto = UserDto.builder()
                .name("NameTest")
                .email("test@mail.ru").build();

        UserDto checkUserDto = userService.createUser(userDto);
        Assertions.assertEquals(1L, checkUserDto.getId(), "Поля объектов не совпадают");
        Assertions.assertEquals(checkUserDto.getName(), userDto.getName(), "Поля объектов не совпадают");
        Assertions.assertEquals(checkUserDto.getEmail(), userDto.getEmail(), "Поля объектов не совпадают");
    }

    @Test
    void testCreateUserWithEmptyBodyRequest() {

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> userService.createUser(null));

        Assertions.assertEquals("Тело запроса не может быть пустым.", exception.getMessage());
    }

    @Test
    void testCreateUserWithEmptyEmail() {
        UserDto userDto = UserDto.builder()
                .name("NameTest").build();

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> userService.createUser(userDto));

        Assertions.assertEquals("Адрес электронной почты не может быть пустым", exception.getMessage());
    }

    @Test
    void testCreateUserWithIncorrectEmail() {
        UserDto userDto = UserDto.builder()
                .name("NameTest")
                .email("test.mail.ru").build();

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> userService.createUser(userDto));

        Assertions.assertEquals("Адрес электронной почты должен содержать символ @", exception.getMessage());
    }

    @Test
    void testUpdateUser() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1L, "NameTest", "test@mail.ru")));
        Mockito
                .when(mockUserRepository.save(Mockito.any()))
                .thenReturn(new User(1L, "UpdateTest", "UpdateTest@mail.ru"));

        UserDto userDto = UserDto.builder()
                .name("UpdateTest")
                .email("UpdateTest@mail.ru").build();

        UserDto checkUserDto = userService.updateUser(1L, userDto);

        Assertions.assertEquals(checkUserDto.getName(), userDto.getName(), "Поля объектов не совпадают");
        Assertions.assertEquals(checkUserDto.getEmail(), userDto.getEmail(), "Поля объектов не совпадают");
    }

    @Test
    void testUpdateUserWithoutFieldName() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1L, "NameTest", "test@mail.ru")));
        Mockito
                .when(mockUserRepository.save(Mockito.any()))
                .thenReturn(new User(1L, "NameTest", "UpdateTest@mail.ru"));

        UserDto userDto = UserDto.builder()
                .email("UpdateTest@mail.ru").build();

        UserDto checkUserDto = userService.updateUser(1L, userDto);

        Assertions.assertEquals(checkUserDto.getName(), "NameTest", "Поля объектов не совпадают");
        Assertions.assertEquals(checkUserDto.getEmail(), userDto.getEmail(), "Поля объектов не совпадают");
    }

    @Test
    void testUpdateUserWithoutFieldEmail() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1L, "NameTest", "test@mail.ru")));
        Mockito
                .when(mockUserRepository.save(Mockito.any()))
                .thenReturn(new User(1L, "UpdateTest", "test@mail.ru"));

        UserDto userDto = UserDto.builder()
                .name("UpdateTest")
                .email(null).build();

        UserDto checkUserDto = userService.updateUser(1L, userDto);

        Assertions.assertEquals(checkUserDto.getName(), userDto.getName(), "Поля объектов не совпадают");
        Assertions.assertEquals(checkUserDto.getEmail(), "test@mail.ru", "Поля объектов не совпадают");
    }

    @Test
    void testUpdateUserWithDuplicateEmail() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1L, "NameTest", "test@mail.ru")));
        Mockito
                .when(mockUserRepository.findByEmailContainingIgnoreCase(Mockito.anyString()))
                .thenThrow(new DuplicateEmailUserException("Пользователь с электронной "
                        + "почтой UpdateTest@mail.ru уже зарегистрирован в приложении"));

        UserDto userDto = UserDto.builder()
                .name("UpdateTest")
                .email("UpdateTest@mail.ru").build();

        final DuplicateEmailUserException exception = Assertions.assertThrows(
                DuplicateEmailUserException.class,
                () -> userService.updateUser(1L, userDto));

        Assertions.assertEquals("Пользователь с электронной "
                + "почтой UpdateTest@mail.ru уже зарегистрирован в приложении", exception.getMessage());
    }

    @Test
    void testFindUserById() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1L, "NameTest", "test@mail.ru")));

        UserDto checkUserDto = userService.findUserById(1L);

        Assertions.assertEquals(1L, checkUserDto.getId(), "Поля объектов не совпадают");
        Assertions.assertEquals(checkUserDto.getName(), "NameTest", "Поля объектов не совпадают");
        Assertions.assertEquals(checkUserDto.getEmail(), "test@mail.ru", "Поля объектов не совпадают");
    }

    @Test
    void testFindUserByIdWithIncorrectId() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenThrow(new UserNotFoundException("Пользователь с id 9999 не зарегистрирован "
                        + "в базе приложения."));

        final UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> userService.findUserById(9999));

        Assertions.assertEquals("Пользователь с id 9999 не зарегистрирован в базе приложения.", exception.getMessage());
    }

    @Test
    void testFindAllUsers() {
        List<User> users = Stream.of(new User(1L, "NameTest", "test@mail.ru"),
                new User(2L, "Name", "testMail@mail.ru")).collect(Collectors.toList());

        Mockito
                .when(mockUserRepository.findAll())
                .thenReturn(users);
        List<UserDto> checkUsers = userService.findAllUsers();

        Assertions.assertEquals(2, checkUsers.size(), "Размеры списков не совпадают");
        Assertions.assertEquals(checkUsers.get(0).getName(), "NameTest", "Поля объектов не совпадают");
        Assertions.assertEquals(checkUsers.get(1).getEmail(), "testMail@mail.ru", "Поля объектов не совпадают");
    }
}
