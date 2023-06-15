package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public Item addNewItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                    @RequestBody ItemDto itemDto) {
        return itemService.addNewItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable long itemId,
                           @RequestBody ItemDto itemDto) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto findItemById(@PathVariable long itemId) {
        return itemService.findItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllItemsByOwnerId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemBySearch(@RequestParam(name = "text") @NotBlank String text) {
        return itemService.getItemBySearch(text);
    }
}
