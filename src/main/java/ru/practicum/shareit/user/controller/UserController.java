package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
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
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<UserDto> findAllUsers() {

        return userService.findAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto findUserById(@PathVariable("userId") long userId) {

        return userService.findUserById(userId);
    }

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {

        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto userDto,
                           @PathVariable("userId") long userId) {

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
