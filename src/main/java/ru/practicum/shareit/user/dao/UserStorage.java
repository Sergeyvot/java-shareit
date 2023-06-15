package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {

    User addUser(UserDto userDto);

    void removeUser(long id);

    void deleteAllUsers();

    User updateUser(long userId, UserDto updateUser);

    Collection<UserDto> getAllUsers();

    UserDto findUserById(long id);
}
