package ru.practicum.shareit.user;


import org.apache.commons.lang3.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationExceptionGateway;
import ru.practicum.shareit.user.dto.UserRequestDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserClientController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody UserRequestDto userDto) {
        checkValidationGatewayUser(userDto);
        log.info("Creating user {}", userDto);
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable long userId,
                              @RequestBody UserRequestDto userDto) {
        log.info("Updating user {}", userId);
        return userClient.updateUser(userId, userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable long userId) {
        log.info("Get user {}", userId);
        return userClient.getUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Get all users");
        return userClient.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable long userId) {
        log.info("Delete user {}", userId);
        return userClient.deleteUser(userId);
    }

    private void checkValidationGatewayUser(UserRequestDto userDto) throws ValidationExceptionGateway {

        if (userDto == null) {
            log.error("Передано пустое тело запроса");
            throw new ValidationExceptionGateway("Тело запроса не может быть пустым.");
        }
        if (StringUtils.isBlank(userDto.getEmail())) {
            log.error("Передан некорректный адрес электронной почты: {}", userDto.getEmail());
            throw new ValidationExceptionGateway("Адрес электронной почты не может быть пустым");
        }
        if (StringUtils.containsNone(userDto.getEmail(), "@")) {
            log.error("Передан некорректный адрес электронной почты: {}", userDto.getEmail());
            throw new ValidationExceptionGateway("Адрес электронной почты должен содержать символ @");
        }
    }
}
