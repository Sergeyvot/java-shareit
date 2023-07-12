package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailUserException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository repository, UserMapper userMapper) {
        this.repository = repository;
        this.userMapper = userMapper;
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = repository.findAll();
        log.info("Запрошен список всех пользователей приложения");
        return users.stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findUserById(long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id %d не зарегистрирован "
                        + "в базе приложения.", id)));
        log.info("Запрошен пользователь с id {}. Данные получены", id);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        checkValidationUser(userDto);

        User user = repository.save(userMapper.toUser(userDto));

        log.info("Добавлен пользователь с id {}", user.getId());
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        UserDto user = this.findUserById(userId);
        if (!user.getEmail().equals(userDto.getEmail())) {
            checkDuplicateEmailUser(userDto.getEmail());
        }
        UserDto updateUser = user.toBuilder()
                .name(userDto.getName() != null ? userDto.getName() : user.getName())
                .email(userDto.getEmail() != null ? userDto.getEmail() : user.getEmail()).build();
        log.info("Обновлен пользователь с id {}", userId);
        return userMapper.toUserDto(repository.save(userMapper.toUser(updateUser)));
    }

    @Override
    public UserDto removeUser(long userId) {
        UserDto user = this.findUserById(userId);
        if (user != null) {
            repository.deleteById(userId);
        } else {
            log.error("Передан некорректный id пользователя: {}", userId);
            throw new UserNotFoundException(String.format("Пользователь с id %d не зарегистрирован "
                    + "в базе приложения.", userId));
        }
        return user;
    }

    private void checkValidationUser(UserDto userDto) throws ValidationException {

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
    }

    private void checkDuplicateEmailUser(String email) {
        Optional<User> checkUser = repository.findByEmailContainingIgnoreCase(email);
        if (checkUser.isPresent()) {
            log.error("Адрес электронной почты {} уже есть в приложении.", email);
            throw new DuplicateEmailUserException(String.format("Пользователь с электронной "
                    + "почтой %s уже зарегистрирован в приложении", email));
        }
    }
}
