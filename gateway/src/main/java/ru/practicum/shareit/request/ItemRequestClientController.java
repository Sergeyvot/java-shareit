package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationExceptionGateway;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestClientController {
    private final ItemRequestClient itemRequestClient;
    private static final String CONSTANT_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader(CONSTANT_HEADER) Long userId,
                                        @RequestBody RequestDto requestDto) {
        if (requestDto == null) {
            log.error("Передано пустое тело запроса");
            throw new ValidationExceptionGateway("Тело запроса не может быть пустым.");
        }
        if (StringUtils.isBlank(requestDto.getDescription()) ||
                requestDto.getDescription() == null) {
            log.error("Передано некорректное описание вещи: {}", requestDto.getDescription());
            throw new ValidationExceptionGateway("Описание вещи не может быть пустым");
        }
        log.info("Creating request {}, userId={}", requestDto, userId);
        return itemRequestClient.addRequest(userId,requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader(CONSTANT_HEADER) Long userId) {
        log.info("Get request with userId={}", userId);
        return itemRequestClient.getRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader(CONSTANT_HEADER) Long userId,
                                             @PathVariable long requestId) {
        log.info("Get request {}, userId={}", requestId, userId);
        return itemRequestClient.getRequest(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(CONSTANT_HEADER) Long userId,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        checkPaginationParamsGateway(from, size);
        log.info("Get item userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.getAllRequests(userId, from, size);
    }

    private void checkPaginationParamsGateway(Integer from, Integer size) {
        if (from < 0 || size < 0 || (from.equals(0) && size.equals(0))) {
            log.error("Переданы некорректные параметры постраничного вывода");
            throw new ValidationExceptionGateway("Переданы некорректные параметры постраничного вывода");
        }
    }
}
