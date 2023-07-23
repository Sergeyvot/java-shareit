package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

public final class ItemMapperUtil {

    private ItemMapperUtil() {
    }

    public static Item toItem(ItemDto itemDto, ItemRequest itemRequest) {

        Item.ItemBuilder item = Item.builder();

        if (itemDto.getId() != null) {
            item.id(itemDto.getId());
        }
        item.name(itemDto.getName());
        item.description(itemDto.getDescription());
        if (itemDto.getAvailable() != null) {
            item.available(itemDto.getAvailable());
        }
        if (itemRequest != null) {
            item.request(itemRequest);
        }

        return item.build();
    }
}
