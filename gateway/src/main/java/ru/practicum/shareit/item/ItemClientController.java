package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationExceptionGateway;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemClientController {
    private final ItemClient itemClient;
    private static final String CONSTANT_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(CONSTANT_HEADER) long userId,
                                           @RequestBody @Valid ItemRequestDto itemDto) {
        checkValidationItemGateway(itemDto);
        log.info("Adding item {}, userId={}", itemDto, userId);
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(CONSTANT_HEADER) Long userId,
                                             @PathVariable long itemId,
                                             @RequestBody ItemRequestDto itemDto) {
        log.info("Updating item {}, userId={}", itemId, userId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsOwner(@RequestHeader(CONSTANT_HEADER) Long userId,
                                                @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        checkPaginationParamsGateway(from, size);
        log.info("Get item userId={}, from={}, size={}", userId, from, size);
        return itemClient.getItemsOwner(userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(CONSTANT_HEADER) Long userId,
                                    @PathVariable long itemId,
                                    @RequestBody CommentRequestDto commentDto) {
        log.info("Adding comment {}, userId={}", commentDto, userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(CONSTANT_HEADER) Long userId,
                                       @PathVariable long itemId) {
        log.info("Get item {}, userId={}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemBySearch(@RequestHeader(CONSTANT_HEADER) Long userId,
                                         @RequestParam(name = "text") String text,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get item with text={}, from={}, size={}", text, from, size);
        return itemClient.getItemBySearch(userId, text, from, size);
    }

    private void checkValidationItemGateway(ItemRequestDto itemDto) throws ValidationExceptionGateway {

        if (itemDto == null) {
            log.error("Передано пустое тело запроса");
            throw new ValidationExceptionGateway("Тело запроса не может быть пустым.");
        }
        if (StringUtils.isBlank(itemDto.getName())) {
            log.error("Передано некорректное название вещи: {}", itemDto.getName());
            throw new ValidationExceptionGateway("Название вещи не может быть пустым");
        }
        if (StringUtils.isBlank(itemDto.getDescription())) {
            log.error("Передано некорректное описание вещи: {}", itemDto.getDescription());
            throw new ValidationExceptionGateway("Описание вещи не может быть пустым");
        }
        if (itemDto.getAvailable() == null) {
            log.error("Доступность вещи не указана");
            throw new ValidationExceptionGateway("При создании вещи должна быть указана ее доступность. "
                    + "Поле не может быть пустым");
        }
    }

    private void checkPaginationParamsGateway(Integer from, Integer size) {
        if (from < 0 || size < 0 || (from.equals(0) && size.equals(0))) {
            log.error("Переданы некорректные параметры постраничного вывода");
            throw new ValidationExceptionGateway("Переданы некорректные параметры постраничного вывода");
        }
    }
}
