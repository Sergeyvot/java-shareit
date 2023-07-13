package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private final String CONSTANT_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto addNewItem(@RequestHeader(CONSTANT_HEADER) Long userId,
                              @RequestBody ItemDto itemDto) {
        ItemDto result = itemService.addNewItem(userId, itemDto);
        if (result != null) {
            log.info("Запрос выполнен. В приложение добавлена вещь с id {}", result.getId());
        } else {
            log.info("Добавление в приложение вещи {} не удалось", itemDto.getName());
        }
        return result;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addNewComment(@RequestHeader(CONSTANT_HEADER) Long userId,
                                 @PathVariable long itemId,
                                 @RequestBody CommentDto commentDto) {
        CommentDto result = itemService.addNewComment(userId, itemId, commentDto);
        if (result != null) {
            log.info("В приложение добавлен отзыв на аренду вещи с id {}", itemId);
        } else {
            log.info("Добавление отзыва на аренду вещи с id {} не удалось", itemId);
        }
        return result;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(CONSTANT_HEADER) Long userId,
                              @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) {
        ItemDto result = itemService.updateItem(userId, itemId, itemDto);
        if (result != null) {
            log.info("В приложении отредактирована вещь с id {}", itemId);
        } else {
            log.info("Редактирование вещи с id {} не удалось. Определите ошибку", itemId);
        }
        return result;
    }

    @GetMapping("/{itemId}")
    public ItemDtoBooking findItemById(@RequestHeader(CONSTANT_HEADER) Long userId,
                                       @PathVariable long itemId) {
        ItemDtoBooking result = itemService.findItemById(itemId, userId);
        if (result != null) {
            log.info("Запрошена вещь с id {}. Данные получены", itemId);
        } else {
            log.info("Запрос вещи с id {} не выполнен. Необходимо определить ошибку", itemId);
        }
        return result;
    }

    @GetMapping
    public List<ItemDtoBooking> getAllItemsByOwnerId(@RequestHeader(CONSTANT_HEADER) Long userId,
                                                     @RequestParam(defaultValue = "0", required = false) Integer from,
                                                     @RequestParam(defaultValue = "20", required = false) Integer size) {
        List<ItemDtoBooking> resultList = itemService.getAllItemsByOwnerId(userId, from, size);
        if (resultList != null) {
            log.info("Пользователем с id {} запрошен список своих вещей. Данные получены", userId);
        } else {
            log.info("Запрос списка своих вещей пользователем с id {} не выполнен. Необходимо определить ошибку",
                    userId);
        }
        return resultList;
    }

    @GetMapping("/search")
    public List<ItemDto> getItemBySearch(@RequestParam(name = "text") @NotBlank String text,
                                         @RequestParam(defaultValue = "0", required = false) Integer from,
                                         @RequestParam(defaultValue = "20", required = false) Integer size) {
        List<ItemDto> resultList = itemService.getItemBySearch(text, from, size);
        if (resultList != null) {
            log.info("Запрошен список вещей, содержащих в названии или описании - {}. Данные получены", text);
        } else {
            log.info("Запрос списка вещей, содержащих в названии или описании - {}, не выполнен. " +
                            "Необходимо определить ошибку", text);
        }
        return resultList;
    }
}
