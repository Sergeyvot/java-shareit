package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dao.UserStorageImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorageImpl userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public Collection<UserDto> findAllUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public UserDto findUserById(long id) {
        return userStorage.findUserById(id);
    }

    @Override
    public User createUser(UserDto userDto) {
        return userStorage.addUser(userDto);
    }

    @Override
    public User updateUser(long userId, UserDto userDto) {
        return userStorage.updateUser(userId, userDto);
    }

    @Override
    public void removeUser(long id) {
        userStorage.removeUser(id);
    }

    @Override
    public void deleteAllUsers() {
        userStorage.deleteAllUsers();
    }
}
