package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemStorage {

    Item addNewItem(User user, ItemDto itemDto);

    Item updateItem(long userId, long itemId, ItemDto itemDto);

    ItemDto findItemById(long itemId);

    List<ItemDto> getAllItemsByOwnerId(long userId);

    List<ItemDto> getItemBySearch(String text);
}
