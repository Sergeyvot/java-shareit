package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<UserDto> findAllUsers() {
        Collection<UserDto> resultList = userService.findAllUsers();
        if (resultList != null) {
            log.info("Запрошен список всех пользователей приложения. Данные получены");
        } else {
            log.info("Запрос списка всех пользователей приложения не выполнен. Необходимо определить ошибку");
        }
        return resultList;
    }

    @GetMapping("/{userId}")
    public UserDto findUserById(@PathVariable("userId") long userId) {
        UserDto result = userService.findUserById(userId);
        if (result != null) {
            log.info("Запрошен пользователь с id {}. Данные получены", userId);
        } else {
            log.info("Запрос пользователя с id {} не выполнен. Необходимо определить ошибку", userId);
        }
        return result;
    }

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        UserDto result = userService.createUser(userDto);
        if (result != null) {
            log.info("В приложение добавлен пользователь с id {}", result.getId());
        } else {
            log.info("Добавление в приложение пользователя не выполнено. Необходимо определить ошибку");
        }
        return result;
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto userDto,
                           @PathVariable("userId") long userId) {
        UserDto result = userService.updateUser(userId, userDto);
        if (result != null) {
            log.info("Обновлены данные пользователя с id {}", userId);
        } else {
            log.info("Обновление данных пользователя с id {} не выполнено. Необходимо определить ошибку", userId);
        }
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{id}")
    public void removeUser(@PathVariable("id") long id) {
        userService.removeUser(id);
    }

    @DeleteMapping
    public void deleteAllUsers() {
        userService.deleteAllUsers();
    }
}
