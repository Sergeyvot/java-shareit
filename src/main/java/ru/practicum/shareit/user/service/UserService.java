package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {

    Collection<UserDto> findAllUsers();

    UserDto findUserById(long id);

    User createUser(UserDto userDto);

    User updateUser(long userId, UserDto userDto);

    void removeUser(long id);

    void deleteAllUsers();
}
