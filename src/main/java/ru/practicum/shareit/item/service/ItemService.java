package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item addNewItem( long userId, ItemDto itemDto);

    Item updateItem(long userId, long itemId, ItemDto itemDto);

    ItemDto findItemById(long itemId);

    List<ItemDto> getAllItemsByOwnerId(long userId);

    List<ItemDto> getItemBySearch(String text);
}
