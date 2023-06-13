package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicateEmailUserException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class UserStorageImpl implements UserStorage {
    @Autowired
    private UserMapper userMapper;
    private final Map<Long, User> users = new HashMap<>();
    private long id;

    public Map<Long, User> getUsers() {
        return users;
    }

    @Override
    public User addUser(UserDto userDto) {
        User user = userMapper.toUser(checkValidationUser(userDto));
        checkDuplicateEmailUser(user.getEmail());

        long userId = ++id;
        User newUser = user.toBuilder().id(userId).build();
        users.put(userId, newUser);
        log.info("Добавлен пользователь с id {}", userId);
        return newUser;
    }

    @Override
    public void removeUser(long id) {
        if (users.containsKey(id)) {
            users.remove(id);
            log.info("Удален пользователь с id {}", id);
        } else {
            log.error("Передан некорректный id пользователя: {}", id);
            throw new UserNotFoundException(String.format("Пользователь с id %d не существует.", id));
        }
    }

    @Override
    public void deleteAllUsers() {
        users.clear();
        log.info("Удалены все пользователи приложения");
    }

    @Override
    public User updateUser(long userId, UserDto updateUserDto) {

        if (users.containsKey(userId)) {
            if (!users.get(userId).getEmail().equals(updateUserDto.getEmail())) {
                checkDuplicateEmailUser(updateUserDto.getEmail());
            }
            User updateUser = users.get(userId).toBuilder()
                    .name(updateUserDto.getName() != null ? updateUserDto.getName() : users.get(userId).getName())
                    .email(updateUserDto.getEmail() != null ? updateUserDto.getEmail() : users.get(userId).getEmail())
                    .build();
            users.put(userId, updateUser);
            log.info("Обновлен пользователь с id {}", userId);
            return updateUser;
        } else {
            log.error("Передан некорректный id пользователя: {}", userId);
            throw new UserNotFoundException(String.format("Пользователь с id %d не зарегистрирован "
                    + "в базе приложения.", userId));
        }
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        log.info("Запрошен список всех пользователей приложения");
        return users.values().stream()
                .map(user -> userMapper.toUserDto(user))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findUserById(long id) {
        if (users.containsKey(id)) {
            log.info("Запрошен пользователь с id {}. Данные получены", id);
            return userMapper.toUserDto(users.get(id));
        } else {
            log.error("Передан некорректный id пользователя: {}", id);
            throw new UserNotFoundException(String.format("Пользователь с id %d не зарегистрирован "
                    + "в базе приложения.", id));
        }
    }

    private UserDto checkValidationUser(UserDto userDto) throws ValidationException {

        if (userDto == null) {
            log.error("Передано пустое тело запроса");
            throw new ValidationException("Тело запроса не может быть пустым.");
        }
        if (StringUtils.isBlank(userDto.getEmail())) {
            log.error("Передан некорректный адрес электронной почты: {}", userDto.getEmail());
            throw new ValidationException("Адрес электронной почты не может быть пустым");
        }
        if (StringUtils.containsNone(userDto.getEmail(), "@")) {
            log.error("Передан некорректный адрес электронной почты: {}", userDto.getEmail());
            throw new ValidationException("Адрес электронной почты должен содержать символ @");
        }
        if (StringUtils.isBlank(userDto.getName())) {
            return userDto.toBuilder().name("unknown").build();
        }
        return userDto;
    }

    private void checkDuplicateEmailUser(String email) {
        boolean checkUsers = users.values().stream()
                .anyMatch(u -> u.getEmail().equals(email));
        if (checkUsers) {
            log.error("Адрес электронной почты {} уже есть в приложении.", email);
            throw new DuplicateEmailUserException(String.format("Пользователь с электронной почтой %s уже зарегистрирован "
                    + "в приложении", email));
        }
    }
}
