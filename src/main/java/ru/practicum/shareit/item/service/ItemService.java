package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto addNewItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    CommentDto addNewComment(long userId, long itemId, CommentDto commentDto);

    ItemDtoBooking findItemById(long itemId, long userId);

    Item findEntityById(long itemId);

    List<ItemDtoBooking> getAllItemsByOwnerId(long userId, Integer from, Integer size);

    List<ItemDto> getItemBySearch(String text, Integer from, Integer size);
}
